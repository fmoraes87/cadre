package com.cadre.server.core.service;

import com.cadre.server.core.boundary.ServiceProvider;
import com.cadre.server.core.dto.RequestDataAccess;

/**
 * Data Access Service
 * @author fernando
 *
 */
public interface DataAccessService extends ServiceProvider {

	static final String CLIENT_NOT_AUTHORIZED = "AppClient/User does not have access";
	static final String APP_NOT_AUTHORIZED = "App not authorized";

	/**
	 * Is Client Allowed ? (User and APP)
	 * @param trxName
	 * @param type
	 * @param resource
	 * @param op
	 * @return
	 */
	boolean isClientAllowed(String trxName, RequestDataAccess request);


}
