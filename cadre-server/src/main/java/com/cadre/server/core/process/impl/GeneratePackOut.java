package com.cadre.server.core.process.impl;

import javax.enterprise.inject.spi.CDI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.annotation.SvrProcess;
import com.cadre.server.core.process.CadreProcess;
import com.cadre.server.core.process.ProcessInfoParameter;
import com.cadre.server.core.service.ModelService;

@SvrProcess(GeneratePackOut.PROCESS_NAME)
public class GeneratePackOut extends CadreProcess {
	

	private static final Logger LOGGER = LoggerFactory.getLogger(GeneratePackOut.class);


	static final String PROCESS_NAME = "generatePackOut";
	static final String PARAM_ENTITY_NAME = "AD_PackOut_ID";
	
	private ModelService genericService;

	public GeneratePackOut() {
		this.genericService = CDI.current().select(ModelService.class).get();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public String getProcessName() {
		return PROCESS_NAME;
	}
		

	
	@Override
	public void prepare() {
		ProcessInfoParameter[] para = getParams();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getName();
			if (para[i].getValue() == null)
				;
			//TODO
			else
				LOGGER.warn("Unknown Parameter: " + name);
		}
		
		
	}

	@Override
	public Object doIt(String trxName){
		return null;
	}
	
	
	

}
