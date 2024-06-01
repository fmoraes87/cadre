package com.cadre.server.core.persistence.exception;

public class DBQuerySyntaxException extends DBException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DBQuerySyntaxException(int status, String code) {
		super(status,code);
	}

	public DBQuerySyntaxException(int statusCode, String code, String message) {
		super(statusCode,code,message);
	}
}
