package com.cadre.server.core.process;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.exception.CadreException;

@Singleton
public class SvrProcessEngine {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SvrProcessEngine.class);


	private static final String MSG_INVALID_PROCESS = "@InvalidProcess@";
	
	@Inject @Any
	private Instance<CadreProcess> svrProcess;
	
	public SvrProcessEngine() {	
	}
	
	
	public CadreProcess getSvrProcess(String processName) {
		
        Instance<CadreProcess> instance = this.svrProcess.select(CadreProcess.class,new ProcessNameLiteral(processName));
        if (instance.isUnsatisfied()) {
        	throw new CadreException(Status.BAD_REQUEST.getStatusCode(),MSG_INVALID_PROCESS);
        }else{
        	return instance.get();
        }
        
	}
	

}
