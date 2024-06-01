package com.cadre.server.core.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.CadreSvrBootstrap;
import com.cadre.server.core.web.servlet.CadreServlet;

@WebListener
public class CadreServerListener implements ServletContextListener{

	private static final Logger LOGGER = LoggerFactory.getLogger(CadreServerListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("Initializing {}", CadreServlet.class);

    	new CadreSvrBootstrap().init();

    }
}
