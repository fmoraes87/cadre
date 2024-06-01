package com.cadre.server.core.process.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.annotation.SvrProcess;
import com.cadre.server.core.process.CadreProcess;
import com.cadre.server.core.process.ProcessInfoParameter;
import com.cadre.server.core.service.ModelService;

@SvrProcess(CopyFieldsFromTable.PROCESS_VALUE)
public class CopyFieldsFromTable extends CadreProcess {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CopyFieldsFromTable.class);


	static final String PROCESS_VALUE = "createFieldsFromTable";
	
	
	private ModelService service;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public String getProcessName() {
		return PROCESS_VALUE;
	}
		

	
	@Override
	public void prepare() {
		ProcessInfoParameter[] para = getParams();
		for (int i = 0; i < para.length; i++)
		{
			//else
			//log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		
	}

	@Override
	public Object doIt(String trxName){
		return null;
	}


}
