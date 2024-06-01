package com.cadre.server.core.odata.server.action;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.api.processor.ActionPrimitiveProcessor;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResourceAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.converters.DefaultPopulatingConverter;
import com.cadre.server.core.converters.Populator;
import com.cadre.server.core.entity.MProcess;
import com.cadre.server.core.exception.CadreException;
import com.cadre.server.core.persistence.jdbc.Trx;
import com.cadre.server.core.process.CadreProcess;
import com.cadre.server.core.process.ProcessInfoParameter;
import com.cadre.server.core.process.ScriptingEngine;
import com.cadre.server.core.process.SvrProcessEngine;
import com.cadre.server.core.process.SvrProcessResponse;
import com.cadre.server.core.service.ModelService;

@Singleton
public class ODataActionProcessor implements ActionPrimitiveProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ODataActionProcessor.class);
	public static final String ACTION_PREFIX_NAME = "cadreAction_";

	private OData odata;
	private ServiceMetadata serviceMetadata;

	@Inject
	private ModelService modelService;
	
	@Inject
	private SvrProcessEngine svrEngine;

	private DefaultPopulatingConverter<Parameter, ProcessInfoParameter> defaultConverter;

	@Override
	public void init(OData odata, ServiceMetadata serviceMetadata) {
		this.odata = odata;
		this.serviceMetadata = serviceMetadata;
		initConverter();

	}

	private void initConverter() {
		this.defaultConverter = new DefaultPopulatingConverter<>();
		defaultConverter.setTargetClass(ProcessInfoParameter.class);
		Populator<Parameter, ProcessInfoParameter> defaultPopulator = new Populator<Parameter, ProcessInfoParameter>() {
			
			@Override
			public void populate(Parameter source, ProcessInfoParameter target) {
				target.setName(source.getName());
				target.setValue(source.getValue());
				
			}
		};
				
		defaultConverter.setPopulators(Arrays.asList(defaultPopulator));	

		
	}

	protected boolean isODataMetadataNone(final ContentType contentType) {
		return contentType.isCompatible(ContentType.APPLICATION_JSON) && ContentType.VALUE_ODATA_METADATA_NONE
				.equalsIgnoreCase(contentType.getParameter(ContentType.PARAMETER_ODATA_METADATA));
	}

	@Override
	public void processActionPrimitive(ODataRequest request, ODataResponse response, UriInfo uriInfo,
			ContentType requestFormat, ContentType responseFormat)
			throws ODataApplicationException, ODataLibraryException {
		// 1st Get the action from the resource path
		final EdmAction edmAction = ((UriResourceAction) uriInfo.asUriInfoResource().getUriResourceParts().get(0))
				.getAction();

		// 2nd Deserialize the parameter
		// In our case there is only one action. So we can be sure that parameter
		// "Amount" has been provided by the client
		if (requestFormat == null) {
			throw new ODataApplicationException("The content type has not been set in the request.",
					HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
		}

		final ODataDeserializer deserializer = odata.createDeserializer(requestFormat);
		final Map<String, Parameter> actionParameter = deserializer.actionParameters(request.getBody(), edmAction)
				.getActionParameters();

		List<Parameter> params = actionParameter.values().stream().collect(Collectors.toList());

		SvrProcessResponse actionResponse = executeProcess(edmAction, actionParameter, params);
		Property property = new Property(null, "response", ValueType.PRIMITIVE, actionResponse.getBody());

		EdmPrimitiveType type = (EdmPrimitiveType) edmAction.getReturnType().getType();

		final ContextURL contextURL = ContextURL.with().type(type).build();
		final PrimitiveSerializerOptions options = PrimitiveSerializerOptions.with().contextURL(contextURL).build();
		final SerializerResult result = odata.createSerializer(responseFormat).primitive(serviceMetadata, type,
				property, options);
		response.setContent(result.getContent());
		response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
		response.setStatusCode(actionResponse.getStatus().getStatusCode());

	}

	private SvrProcessResponse executeProcess(final EdmAction edmAction, final Map<String, Parameter> actionParameter,
			List<Parameter> params) {
		String trxName = CadreEnv.getTrxName();
		SvrProcessResponse response = null;
		try {

			String adProcessValue = edmAction.getName().substring(ODataActionProcessor.ACTION_PREFIX_NAME.length());
			MProcess process = modelService.getPO(trxName, MProcess.TABLE_NAME,MProcess.COLUMNNAME_Value, adProcessValue);
			if (process.getAD_Scripting_ID() > 0) {
				ScriptingEngine.execute(trxName, process.getAD_Scripting_ID());
			} else {
				CadreProcess proc = svrEngine.getSvrProcess(process.getProcedureName());
				List<ProcessInfoParameter> convertedParams = this.defaultConverter.convertAll(params);
				proc.setParams(convertedParams.stream().toArray(ProcessInfoParameter[]::new));
				Optional<Object> objResponse = proc.execute(trxName);
						
						
				response= new SvrProcessResponse() {

					@Override
					public Status getStatus() {
						return Status.OK;
					}

					@Override
					public Object getBody() {
						return objResponse.isPresent()?objResponse.get():"Success!";
					}
					
				};
			}

			return response;

		} catch (CadreException ex) {
			LOGGER.error("executeProcess.: " + edmAction.getName());
			Trx.get(trxName, false).rollback(false);
			throw ex;

		}

	}
}
