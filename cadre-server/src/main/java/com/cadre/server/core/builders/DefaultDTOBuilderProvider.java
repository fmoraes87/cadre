package com.cadre.server.core.builders;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultDTOBuilderProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDTOBuilderProvider.class);

	
	private Map<String,List<Class<? extends CustomCadreDTOBuilder>>>	_builders = new Hashtable<>();	
	
	// Inner class to provide instance of class
	private static class Singleton {
		private static final DefaultDTOBuilderProvider INSTANCE = new DefaultDTOBuilderProvider();
	}

	public static DefaultDTOBuilderProvider get() {
		return Singleton.INSTANCE;
	}

	private DefaultDTOBuilderProvider() {
	}


	public CsdlEntityType build(String entityTypeName) {
		
		CsdlEntityType entityType = new CsdlEntityType();
		
		
		List<Class<? extends CustomCadreDTOBuilder>>  list = _builders.get(entityTypeName);
		if (list != null)
		{
			for (Class<? extends CustomCadreDTOBuilder> clazzBuilder: list) {
				CustomCadreDTOBuilder builder;
				try {
					builder = clazzBuilder.newInstance();
					builder.build(entityTypeName,entityType);
				} catch (InstantiationException | IllegalAccessException e) {
					LOGGER.error("build("+entityTypeName+")", e);
					list.remove(clazzBuilder);

					DefaultDTOBuilder defaultBuilder = new DefaultDTOBuilder();
					defaultBuilder.build(entityTypeName, entityType);
				}
			}
		}else {
			DefaultDTOBuilder defaultBuilder = new DefaultDTOBuilder();
			defaultBuilder.build(entityTypeName, entityType);
		}
		
		return entityType;


	}

	public  <T extends CustomCadreDTOBuilder> void add(String resource, Class<T> builder) {
		if (resource == null || builder == null) {
			return;			
		}

		List<Class<? extends CustomCadreDTOBuilder>> list = _builders.get(resource);
		if (list == null)
		{
			list = new ArrayList<>();
			list.add(builder);
			_builders.put(resource, list);
		}
		else {
			list.add(builder);			
		}		
	}
}
