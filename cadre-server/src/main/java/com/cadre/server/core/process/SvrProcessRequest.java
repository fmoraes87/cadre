package com.cadre.server.core.process;

import com.cadre.server.core.dto.CadreServerRequest;

public interface SvrProcessRequest extends CadreServerRequest {

	public String getProcessName();

	public ProcessInfoParameter[] getParams();

}
