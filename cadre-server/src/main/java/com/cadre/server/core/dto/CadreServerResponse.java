package com.cadre.server.core.dto;

import javax.ws.rs.core.Response.Status;

public interface CadreServerResponse {

	Status getStatus();

	Object getBody();

}