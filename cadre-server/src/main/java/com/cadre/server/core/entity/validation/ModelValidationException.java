package com.cadre.server.core.entity.validation;

import javax.ws.rs.core.Response.Status;

import com.cadre.server.core.exception.CadreException;

public class ModelValidationException extends CadreException {

	public ModelValidationException(String code) {
		super(Status.BAD_REQUEST.getStatusCode(),code);
	}
	
	public ModelValidationException(int status, String code) {
		super(status,code);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
