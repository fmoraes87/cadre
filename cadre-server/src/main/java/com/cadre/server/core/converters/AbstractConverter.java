package com.cadre.server.core.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConverter<SOURCE,TARGET> implements Converter<SOURCE,TARGET>, Populator<SOURCE,TARGET> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConverter.class);
	
	private Class<TARGET> targetClass;
	
	@Override
	public TARGET convert (final SOURCE source) {
		final TARGET target = createFromClass();
		populate(source,target);
		
		return target;
	}
	
	public TARGET convert (final SOURCE source, final TARGET prototype) {
		populate(source,prototype);
		return prototype;
	}
	
	public void setTargetClass(Class<TARGET> targetClass) {
		this.targetClass = targetClass;
	}

	protected TARGET createFromClass() {
		try {
			return targetClass.newInstance();
		}catch(final InstantiationException | IllegalAccessException e) {
			LOGGER.error("createFromClass()", e);
			throw new RuntimeException(e);
		}
	}
	
	
	
}
