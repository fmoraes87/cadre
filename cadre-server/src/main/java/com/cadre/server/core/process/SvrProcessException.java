package com.cadre.server.core.process;

import com.cadre.server.core.exception.CadreException;

public class SvrProcessException extends CadreException {

	public SvrProcessException(int status,String code) {
		super(status,code);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
