package com.cadre.server.core.web.rest;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.apache.commons.lang3.StringUtils;

import com.cadre.server.core.exception.CadreException;
import com.cadre.server.core.util.MessageUtils;

public class CadreExceptionHandler {
	
    /**
     * Build Exception Response
     * @param status
     * @param ex
     * @return
     */
	public static Response buildExceptionResponse(StatusType status, String code) {
		
		JsonObject json = buildJSON(code,null);
		return Response.status(status).entity(json).build();
	}

	
    /**
     * Build Exception Response
     * @param status
     * @param ex
     * @return
     */
	public static Response buildExceptionResponse(CadreException ex) {
		
		JsonObject json = buildJSON(ex.getCode(),ex.getMessage());
		return Response.status(ex.getStatus()).entity(json).build();
	}

	public static JsonObject buildJSON(CadreException ex) {
		return buildJSON(ex.getCode(),ex.getMessage());
	}
	
	public static JsonObject buildJSON(String code, String msg) {
		JsonObject json = Json.createObjectBuilder()
				.add("code", MessageUtils.parseVariable(StringUtils.defaultIfEmpty(code, StringUtils.EMPTY)))
				.add("message", MessageUtils.parseMessage(StringUtils.defaultIfEmpty(code, StringUtils.defaultIfEmpty(msg, StringUtils.EMPTY)))).build();
		return json;
	}

}
