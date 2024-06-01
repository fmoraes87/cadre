package com.cadre.server.core.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.util.SecurityUtils;
import com.cadre.server.core.web.rest.CadreExceptionHandler;

public abstract class SecureCadreServlet extends CadreServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SecureCadreServlet.class);

    @Override
    public void init() {
        LOGGER.info("Initializing {}", SecureCadreServlet.class);
    }


    /**
     * Execute business logic
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    protected abstract void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException ;

    @Override
	final protected void processComponentRequest(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
    	try {
        	
			String[] credentials =  OAuthUtils.decodeClientAuthenticationHeader( req.getHeader(HttpHeaders.AUTHORIZATION));
			
			if (credentials!=null && credentials.length > 1) {
				SecurityUtils.validateClient(null,credentials[0], credentials[1]);
			}else {
				SecurityUtils.validateTokenFrom(req);				
			}
				  
    		execute(req, resp);
    		
    	}catch (OAuthSystemException ex) {
    		JsonObject json = CadreExceptionHandler.buildJSON(ex.getMessage(), null);
    		resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    		resp.setContentType(MediaType.APPLICATION_JSON);
    		resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
    		
			LOGGER.error(json.toString(), ex);

			
    		PrintWriter out = resp.getWriter();
    		out.print(json.toString());
    		out.flush();
    	}
	}
    

}
