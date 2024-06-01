package com.cadre.server.core.process;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

public class WebSvrProcessRequest implements SvrProcessRequest {

	private static final String PROCESS_NAME = "processName";
	protected HttpServletRequest request;

	public WebSvrProcessRequest(HttpServletRequest request){
		this.request = request;
		validate();
	}

	protected void validate(){
		validateRequiredParameters();
	}

    public String getParam(String name) {
        return request.getParameter(name);
    }

	private void validateRequiredParameters() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getProcessName() {
		return getParam(PROCESS_NAME);
	}

	@Override
	public ProcessInfoParameter[] getParams() {
		return Collections.list(request.getParameterNames())
	    .stream()
	    //.filter(parameterName ->parameterName.startsWith("p_"))
	    .map(parameterName -> new ProcessInfoParameter(parameterName,request.getParameter(parameterName)))
	    .toArray(ProcessInfoParameter[]::new);
	}
}
