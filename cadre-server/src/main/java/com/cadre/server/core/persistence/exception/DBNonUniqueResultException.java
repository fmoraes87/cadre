package com.cadre.server.core.persistence.exception;

import javax.ws.rs.core.Response.Status;

public class DBNonUniqueResultException extends DBException{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//"More than one row with the given identifier was found"
	public DBNonUniqueResultException() {
		this(Status.INTERNAL_SERVER_ERROR.getStatusCode(),"@DBNonUniqueResultException@");
	}

	public DBNonUniqueResultException(int status, String code) {
		super(status,code);
	}
}
