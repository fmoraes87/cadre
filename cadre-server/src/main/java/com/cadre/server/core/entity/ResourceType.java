package com.cadre.server.core.entity;

public enum ResourceType {

	TABLE_COLUMN(1),
	PROCESS(2),
	REPORT(3),
	ENDPOINT(4);

	
	private int code;

	ResourceType(int code){
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
	
	
	
	
}
