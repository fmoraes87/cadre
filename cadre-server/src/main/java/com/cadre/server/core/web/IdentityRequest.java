package com.cadre.server.core.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang3.StringUtils;

import com.cadre.server.core.dto.CadreServerRequest;

public class IdentityRequest extends RequestWrapper implements CadreServerRequest {

	private static final String PROVIDER = "provider";
	private static final String DEFAULT_LOGIN_TYPE = "default";

	public IdentityRequest(HttpServletRequest request, MultivaluedMap<String, String> form) {
		super(request, form);
	}

	public boolean isLoginByInternalSystem() {
		String loginProvider = getLoginType();
		return StringUtils.isEmpty(loginProvider);
	}

	public String getLoginType() {
		return StringUtils.defaultIfEmpty(getParameter(PROVIDER), DEFAULT_LOGIN_TYPE) ;
	}
	
	public Map<String,String> getAllInfoAsMap(){
        Map<String, String> map = new HashMap<String, String>();

        Enumeration headerNames = this.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = this.getHeader(key);
            map.put(key, value);
        }
        
        Enumeration enumeration = this.getParameterNames();
        while(enumeration.hasMoreElements()){
            String parameterName = (String) enumeration.nextElement();
            map.put(parameterName, this.getParameter(parameterName));
        }	
        
        

		return map;
	}

}
