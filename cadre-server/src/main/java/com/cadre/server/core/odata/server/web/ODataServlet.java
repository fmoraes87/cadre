package com.cadre.server.core.odata.server.web;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.odata.server.action.ODataActionProcessor;
import com.cadre.server.core.odata.server.service.ODataEdmProvider;
import com.cadre.server.core.odata.server.service.ODataEntityCollectionProcessor;
import com.cadre.server.core.odata.server.service.ODataEntityProcessor;
import com.cadre.server.core.odata.server.service.ODataErrorProcessor;
import com.cadre.server.core.web.servlet.SecureCadreServlet;

@WebServlet(name = ODataServlet.ODATA_SERVLET_NAME, urlPatterns = {"/ODataServlet.svc/*"} , loadOnStartup =  1)
public class ODataServlet extends SecureCadreServlet {

	static final String ODATA_SERVLET_NAME = "ODataServlet";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */

	private static final Logger LOGGER = LoggerFactory.getLogger(ODataServlet.class);
	
	@Inject
	private ODataActionProcessor actionProcessor;
	
	@Inject
	private ODataErrorProcessor errorProcessor;
	
	@Inject
	private ODataEntityProcessor entityProcessor;
	
	@Inject
	private ODataEntityCollectionProcessor entityCollectionProcessor;
	
	@Inject
	private ODataEdmProvider edmProvider;

    @Override
    public void init() {
        LOGGER.info("Initializing {}", ODataServlet.class);
    }


    
	@Override
	protected void execute(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		
		try {
			OData odata = OData.newInstance();
			ServiceMetadata edm = odata.createServiceMetadata(edmProvider, new ArrayList<EdmxReference>());
			ODataHttpHandler handler = odata.createHandler(edm);
			handler.register(entityCollectionProcessor);
			handler.register(entityProcessor);
			 // Register error handler
			handler.register(actionProcessor);
            handler.register(errorProcessor);
            //e-Tag Support
            
			handler.process(req, resp);
			
		} catch (RuntimeException e) {
			LOGGER.error("Server Error occurred in ODataServlet", e);
			throw new ServletException(e);
		}
	}
	
    @Override
    public void destroy() {
        LOGGER.info("Destroying {}", ODataServlet.class);
    }


}
