package com.cadre.server.core.persistence.exception;

import javax.ws.rs.core.Response.Status;

public class DBNoResultException extends DBException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DBNoResultException() {
		this(Status.NOT_FOUND.getStatusCode(),"@NoEntityFound@");
	}
	
	public DBNoResultException(int status, String code) {
		super(status,code);
	}
}
