package com.cadre.server.core.dto;

import com.cadre.server.core.entity.DatabaseOperation;
import com.cadre.server.core.entity.ResourceType;

public class RequestDataAccess {

	private ResourceType type;
	private String resource;
	private DatabaseOperation op;

	public RequestDataAccess(ResourceType type, String resource, DatabaseOperation op) {
		this.type = type;
		this.resource = resource;
		this.op = op;
	}

	public ResourceType getType() {
		return type;
	}

	public String getResource() {
		return resource;
	}

	public DatabaseOperation getOp() {
		return op;
	}

}
