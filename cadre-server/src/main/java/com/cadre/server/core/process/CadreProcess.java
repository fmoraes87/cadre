package com.cadre.server.core.process;

import java.io.Serializable;
import java.util.Optional;

import javax.ws.rs.core.Response.Status;

import org.apache.oltu.oauth2.common.error.OAuthError;

import com.cadre.server.core.dto.RequestDataAccess;
import com.cadre.server.core.entity.DatabaseOperation;
import com.cadre.server.core.entity.ResourceType;
import com.cadre.server.core.exception.CadreException;
import com.cadre.server.core.resolver.DynamicServiceResolver;
import com.cadre.server.core.service.DataAccessService;

public abstract class CadreProcess implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ProcessInfoParameter[] params;

	//protected ModelService service;

	public CadreProcess() {
	}

	public abstract void prepare();

	public abstract Object doIt(String trxName);

	public abstract String getProcessName();

	/**
	 * Process
	 * @param trxName 
	 * 
	 * @return
	 * @throws CadreException
	 */
	public final Optional<Object> execute(String trxName) {
		
		DataAccessService accessService = DynamicServiceResolver.locate(DataAccessService.class);

		RequestDataAccess requestAccess = new RequestDataAccess(ResourceType.PROCESS, getProcessName(),
				DatabaseOperation.WRITE);

		if (accessService.isClientAllowed(trxName, requestAccess)) {
			prepare();
			Object response = doIt(trxName);

			return Optional.ofNullable(response);
		} else {
			throw new CadreException(Status.FORBIDDEN.getStatusCode(), OAuthError.CodeResponse.ACCESS_DENIED);
		}

	}

	public ProcessInfoParameter[] getParams() {
		return params;
	}

	public void setParams(ProcessInfoParameter[] params) {
		this.params = params;
	}

}
