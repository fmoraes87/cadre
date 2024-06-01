package com.cadre.server.core.service;

import javax.ws.rs.core.MultivaluedMap;

import com.cadre.server.core.boundary.ServiceProvider;

public interface WFServiceEngine extends ServiceProvider  {

	/**
	 * 	
	 * @param taskId
	 * @param form 
	 */
	void completeTask(String taskId,MultivaluedMap<String, String> formParams);

}
