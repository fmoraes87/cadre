package com.cadre.server.core.odata.server.service;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.core.uri.parser.FilterParser;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.parser.UriTokenizer;
import org.apache.olingo.server.core.uri.validator.UriValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.entity.MAppRule;
import com.cadre.server.core.persistence.jdbc.RDBMS;
import com.cadre.server.core.persistence.query.GenericCondition;
import com.cadre.server.core.persistence.query.JDBCQueryImpl;
import com.cadre.server.core.persistence.query.SearchQuery;
import com.cadre.server.core.persistence.query.SearchResult;
import com.cadre.server.core.service.ModelService;
import com.cadre.server.core.util.ParserUtil;

@Singleton
public class ODataEntityCollectionProcessor implements EntityCollectionProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ODataEntityCollectionProcessor.class);

	
	@Inject
	private ODataStorage odataStorage;
	
	@Inject
	private ModelService modelService;

	private OData odata;
	private ServiceMetadata serviceMetadata;

	public ODataEntityCollectionProcessor() {
		
	}

	// our processor is initialized with the OData context object
	public void init(OData odata, ServiceMetadata serviceMetadata) {
		this.odata = odata;
		this.serviceMetadata = serviceMetadata;
	}

	// the only method that is declared in the EntityCollectionProcessor interface
	// this method is called, when the user fires a request to an EntitySet
	// in our example, the URL would be:
	// http://localhost:8080/ExampleService1/ExampleServlet1.svc/Products
	public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo,
			ContentType responseFormat) throws ODataApplicationException, SerializerException {

		// 1st we have retrieve the requested EntitySet from the uriInfo object
		// (representation of the parsed service URI)
		List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
		UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0); // in our example, the
																									// first segment is
																									// the EntitySet
		EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
		EdmEntityType edmEntityType = edmEntitySet.getEntityType();
		
		
		// 2nd: fetch the data from backend for this requested EntitySetName // it has
		// to be delivered as EntitySet object
		//List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
		EntityCollection entityCollection = uriInfo!=null ?odataStorage.readEntitySetData(edmEntitySet
				,uriInfo.getFilterOption()
				,uriInfo.getOrderByOption()
				,uriInfo.getSkipOption()
				,uriInfo.getTopOption()
				,uriInfo.getCountOption()
				): odataStorage.readEntitySetData(edmEntitySet);
		
		// apply system query options
		// Note: $select is handled by the lib, we only configure ContextURL +
		// SerializerOptions
		// for performance reasons, it might be necessary to implement the $select manually
		SelectOption selectOption = uriInfo.getSelectOption();
		String selectList = odata.createUriHelper().buildContextURLSelectList(edmEntityType, null, selectOption);
		
		// 3rd: create a serializer based on the requested format (json)
		ODataSerializer serializer = odata.createSerializer(responseFormat);

		// 4th: Now serialize the content: transform from the EntitySet object to
		// InputStream
		ContextURL contextUrl = ContextURL.with()
					.entitySet(edmEntitySet)
					.selectList(selectList)
					.build();

		final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
		EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with()
															.contextURL(contextUrl)
															.count(uriInfo.getCountOption())
															.select(selectOption)
															.id(id)
															.build();
		SerializerResult serializedContent = serializer.entityCollection(serviceMetadata, edmEntityType, entityCollection,
				opts);

		// Finally: configure the response object: set the body, headers and status code
		response.setContent(serializedContent.getContent());
		response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
	}


}
