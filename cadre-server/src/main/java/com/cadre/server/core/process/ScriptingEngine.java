package com.cadre.server.core.process;

import javax.enterprise.inject.spi.CDI;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.entity.MScripting;
import com.cadre.server.core.exception.CadreException;
import com.cadre.server.core.service.ModelService;

public class ScriptingEngine {
	
	//global or login context variable prefix
	public final static String GLOBAL_CONTEXT_PREFIX = "G_";
	//method call arguments prefix
	public final static String ARGUMENTS_PREFIX = "A_";
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScriptingEngine.class);
	
	// Inner class to provide instance of class 
	private static class Singleton {
		private static final ScriptingEngine INSTANCE = new ScriptingEngine();
	}

	public static ScriptingEngine get() {
		return Singleton.INSTANCE;
	}
	
	
	private ScriptingEngine() {	
	}


	public static void execute(String trxName, Integer scriptingId) {
		ModelService service = CDI.current().select(ModelService.class).get();
		MScripting scripting = service.getPO(trxName, MScripting.TABLE_NAME, scriptingId);
		if (scripting.isGroovy()) {
			executeGroovyScript(trxName,scripting.getContent());
		}else {
			LOGGER.error("Engine type not supported " + scripting.getEngineType());
		}
		
	}


	private static void executeGroovyScript(String trxName, String script) {
		String msg = null;
		boolean success = true;
		
		try {
			ScriptEngineManager factory = new ScriptEngineManager();
			ScriptEngine engine = factory.getEngineByName("groovy");
			
			engine.put(ARGUMENTS_PREFIX + "Ctx", CadreEnv.getCtx());
			engine.put(ARGUMENTS_PREFIX + "TrxName", trxName);
			engine.put(ARGUMENTS_PREFIX + "AD_Client_ID", CadreEnv.getAD_Client_ID());
			engine.put(ARGUMENTS_PREFIX + "AD_User_ID", CadreEnv.getAD_User_ID());
			engine.put(ARGUMENTS_PREFIX + "AD_Org_ID", CadreEnv.getAD_User_ID());

			//engine.put(ARGUMENTS_PREFIX + "AD_PInstance_ID", pi.getAD_PInstance_ID());
			//engine.put(ARGUMENTS_PREFIX + "Table_ID", pi.getTable_ID());
			//engine.put(ARGUMENTS_PREFIX + "Record_ID", pi.getRecord_ID());
			
			Object result = engine.eval(script);
			if (null != result) {
				msg = result.toString();
			}
			
			if (null != msg && msg.startsWith("@Error@")) {
				success = false;
			}
			
			if (!success) {
				throw new CadreException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),msg);
			}
			
		} catch (ScriptException e) {
			LOGGER.error("Error executing groovy script: " + e.getMessage());
			throw new CadreException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),e.getMessage());

		}
	}
	

}
