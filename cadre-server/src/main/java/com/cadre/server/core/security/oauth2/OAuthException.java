package com.cadre.server.core.security.oauth2;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import com.cadre.server.core.persistence.exception.DBException;

public class OAuthException extends OAuthSystemException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OAuthException(String msg) {
		super(msg);
	}

	public OAuthException(DBException ex) {
		super(ex);
	}

	public OAuthException(OAuthMessageType messageType) {
		// TODO Auto-generated constructor stub
	}

}
