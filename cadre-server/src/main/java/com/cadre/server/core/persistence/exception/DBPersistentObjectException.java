package com.cadre.server.core.persistence.exception;

public class DBPersistentObjectException extends DBException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DBPersistentObjectException(int status, String code) {
		super(status,code);
	}

	public DBPersistentObjectException(int statusCode, String code, String message) {
		super(statusCode,code,message);
	}
}
