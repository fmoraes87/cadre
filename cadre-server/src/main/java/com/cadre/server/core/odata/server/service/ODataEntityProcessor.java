package com.cadre.server.core.odata.server.service;

import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;

@Singleton
public class ODataEntityProcessor implements EntityProcessor{
	
	
	@Inject
	private ODataStorage storage;
	
	private OData odata;
	private ServiceMetadata serviceMetadata;
	
	
	public ODataEntityProcessor() {
		
	}
	
	// our processor is initialized with the OData context object
	public void init(OData odata, ServiceMetadata serviceMetadata) {
		this.odata = odata;
		this.serviceMetadata = serviceMetadata;
	}

	@Override
	public void readEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat)
			throws ODataApplicationException, ODataLibraryException {
		// 1. retrieve the Entity Type
	    List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
	    // Note: only in our example we can assume that the first segment is the EntitySet
	    UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
	    EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

	    // 2. retrieve the data from backend
	    List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
	    
	    Entity entity = storage.readEntityData(edmEntitySet, keyPredicates);

	    // 3. serialize
	    EdmEntityType entityType = edmEntitySet.getEntityType();

	    ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();
	    // expand and select currently not supported
	    EntitySerializerOptions options = EntitySerializerOptions.with().contextURL(contextUrl).build();

	    ODataSerializer serializer = odata.createSerializer(responseFormat);
	    SerializerResult serializerResult = serializer.entity(serviceMetadata, entityType, entity, options);
	    InputStream entityStream = serializerResult.getContent();

	    //4. configure the response object
	    response.setContent(entityStream);
	    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
	    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());		
	}

	@Override
	public void createEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat,
			ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
		// 1. Retrieve the entity type from the URI
		List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
		UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
		EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
		EdmEntityType edmEntityType = edmEntitySet.getEntityType();

		// 2. create the data in backend
		// 2.1. retrieve the payload from the POST request for the entity to create and
		// deserialize it
		InputStream requestInputStream = request.getBody();
		ODataDeserializer deserializer = this.odata.createDeserializer(requestFormat);
		DeserializerResult result = deserializer.entity(requestInputStream, edmEntityType);
		Entity requestEntity = result.getEntity();
		// 2.2 do the creation in backend, which returns the newly created entity
		Entity createdEntity = storage.createEntityData(edmEntitySet.getEntityType(), requestEntity);

		// 3. serialize the response (we have to return the created entity)
		ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();
		// expand and select currently not supported
		EntitySerializerOptions options = EntitySerializerOptions.with().contextURL(contextUrl).build();

		ODataSerializer serializer = this.odata.createSerializer(responseFormat);
		SerializerResult serializedResponse = serializer.entity(serviceMetadata, edmEntityType, createdEntity, options);

		// 4. configure the response object
		response.setContent(serializedResponse.getContent());
		response.setStatusCode(HttpStatusCode.CREATED.getStatusCode());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
	}

    public void updateEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat,
            ContentType responseFormat) throws ODataApplicationException, DeserializerException, SerializerException {

        // 1. Retrieve the entity set which belongs to the requested entity 
        List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
        // Note: only in our example we can assume that the first segment is the EntitySet
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();

        // 2. update the data in backend
        // 2.1. retrieve the payload from the PUT request for the entity to be updated
        InputStream requestInputStream = request.getBody();
        ODataDeserializer deserializer = odata.createDeserializer(requestFormat);
        DeserializerResult result = deserializer.entity(requestInputStream, edmEntityType);
        Entity requestEntity = result.getEntity();
        // 2.2 do the modification in backend
        List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
        // Note that this updateEntity()-method is invoked for both PUT or PATCH operations
        //HttpMethod httpMethod = request.getMethod();
	    
        Entity entity = storage.updateEntityData(edmEntitySet.getEntityType(), keyPredicates, requestEntity);

       //3. configure the response object
        ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();
        EntitySerializerOptions options = EntitySerializerOptions.with().contextURL(contextUrl).build();
        
	    ODataSerializer serializer = odata.createSerializer(responseFormat);
	    SerializerResult serializerResult = serializer.entity(serviceMetadata, edmEntityType, entity, options);
        InputStream entityStream = serializerResult.getContent();

	    response.setContent(entityStream);
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
	    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());		

    }
	@Override
	public void deleteEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo)
			throws ODataApplicationException, ODataLibraryException {
		  // 1. Retrieve the entity set which belongs to the requested entity
		  List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
		  // Note: only in our example we can assume that the first segment is the EntitySet
		  UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
		  EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

		  // 2. delete the data in backend
		  List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
		  storage.deleteEntityData(edmEntitySet.getEntityType(), keyPredicates);

		  //3. configure the response object
		  response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
		
	}

}
