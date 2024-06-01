package com.cadre.server.core.odata.server.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.inject.Singleton;
import javax.json.Json;
import javax.json.JsonObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.ErrorProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.exception.CadreException;
import com.cadre.server.core.util.ExceptionUtils;
import com.cadre.server.core.util.MessageUtils;
import com.cadre.server.core.web.rest.CadreExceptionHandler;

@Singleton
public class ODataErrorProcessor implements ErrorProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ODataErrorProcessor.class);

	
	@Override
	public void init(OData odata, ServiceMetadata serviceMetadata) {
		LOGGER.debug("init Error Processor");
		
	}

	@Override
	public void processError(ODataRequest request, ODataResponse response, ODataServerError serverError,
			ContentType responseFormat) {
		

        Throwable ex = ExceptionUtils.findException(serverError.getException(), CadreException.class);
        LOGGER.error(serverError.getMessage(),ex);
        if (ex instanceof CadreException) {
        	JsonObject jsonResponse = CadreExceptionHandler.buildJSON((CadreException) ex);
        	InputStream is = new ByteArrayInputStream(jsonResponse.toString().getBytes(Charset.forName(java.nio.charset.StandardCharsets.UTF_8.name())));
        	
            response.setContent(is);
            response.setStatusCode(serverError.getStatusCode());
            response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
        }else {
    		JsonObject jsonResponse = Json.createObjectBuilder()
    				.add("code", MessageUtils.parseVariable(StringUtils.defaultIfEmpty(ex.getMessage(), StringUtils.EMPTY)))
    				.add("message", MessageUtils.parseMessage(StringUtils.defaultIfEmpty(ex.getMessage(), StringUtils.EMPTY))).build();
    		
            response.setContent(new ByteArrayInputStream(jsonResponse.toString().getBytes(Charset.forName(java.nio.charset.StandardCharsets.UTF_8.name()))));
            response.setStatusCode(serverError.getStatusCode());
            response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
        }

	}

}
