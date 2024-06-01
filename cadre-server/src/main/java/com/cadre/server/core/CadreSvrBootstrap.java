package com.cadre.server.core;

import java.util.Properties;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.annotation.CadreExtensionInitializer;
import com.cadre.server.core.init.CadreBoostrap;
import com.cadre.server.core.init.CadreExtensionBootstrap;

public class CadreSvrBootstrap {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CadreSvrBootstrap.class);

	
	private static final String PACKAGENAME_EXT_INIT = "com.cadre.server.core.ext.init";

	public CadreSvrBootstrap() {
		
	}

	//Run this before web application is started
	public void init() {
		CadreEnv.setCtx(new Properties());

		new CadreExtensionBootstrap().init();
		loadSvrExtensionBootstrap();
		
	}

	

	private void loadSvrExtensionBootstrap() {
		Reflections reflections = new Reflections(PACKAGENAME_EXT_INIT);
        Set<Class<?>> services = reflections.getTypesAnnotatedWith(CadreExtensionInitializer.class);
        services.stream().forEach(clazz -> {
        	try {
        		CadreBoostrap bootstrap = (CadreBoostrap) clazz.newInstance();
        		bootstrap.init();
        		
			} catch (InstantiationException | IllegalAccessException e) {
				LOGGER.error("loadSvrExtensionBootstrap()", e);
				System.exit(1);
			}
        });
		
	}



}
