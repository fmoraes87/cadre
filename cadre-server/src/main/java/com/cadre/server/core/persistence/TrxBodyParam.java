package com.cadre.server.core.persistence;

public class TrxBodyParam {
	
	private String name;
	private Object value;

	public TrxBodyParam(String name, Object value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}


}
