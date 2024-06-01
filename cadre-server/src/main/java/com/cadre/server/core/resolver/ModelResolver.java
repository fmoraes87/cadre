package com.cadre.server.core.resolver;

import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.boundary.CadreFactory;
import com.cadre.server.core.entity.GenericPOModel;
import com.cadre.server.core.entity.POModel;

public class ModelResolver implements CadreFactory<String, Class<? extends POModel>>{

	private static final Logger LOGGER = LoggerFactory.getLogger(ModelResolver.class);

	private Map<String, Class<? extends POModel>>	m_modelFactoryList = new Hashtable<>();


	// Inner class to provide instance of class
	private static class Singleton {
		private static final ModelResolver INSTANCE = new ModelResolver();
	}

	public static ModelResolver get() {
		return Singleton.INSTANCE;
	}

	private ModelResolver() {

	}

	@Override
	public void load(String key, Class<? extends POModel> clazz){

		m_modelFactoryList.put(key, clazz);
	}
	

	/**
	 * 
	 * @param tableName
	 * @return
	 */
	public POModel resolve(String tableName) {
		Class<? extends POModel>  clazz = getElement(tableName);
		if (clazz!=null) {
			try {
				return (POModel) clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				LOGGER.error("resolve"+tableName+")", e);
				return new GenericPOModel();
			}
		}

		return new GenericPOModel();
	}

	
	/**
	 * 
	 * @param tableName
	 * @return
	 */
	public  Class<? extends POModel> getElement(String tableName) {
		return  m_modelFactoryList.get(tableName);
	}

	

}
