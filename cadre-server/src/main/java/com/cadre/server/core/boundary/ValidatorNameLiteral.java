package com.cadre.server.core.boundary;

import javax.enterprise.util.AnnotationLiteral;

import com.cadre.server.core.annotation.StaticValidator;

public class ValidatorNameLiteral extends AnnotationLiteral<StaticValidator> implements StaticValidator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String resourceName;
	
	public ValidatorNameLiteral(String resourceName) {
		this.resourceName = resourceName;
		
	}

	
	@Override
	public String value() {
		return resourceName;
	}

}
