package com.cadre.server.core.web.security;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter(value = {"/*"})
public class CORSFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CORSFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		LOGGER.info("Initializing {}", CORSFilter.class);

	}

	private static final String WHITE_LIST_DOMAINS = "WHITE_LIST_DOMAINS";
	
	private static final String ORIGIN = "Origin";

	private static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
	private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

	/**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
 
        HttpServletRequest request = ((HttpServletRequest) servletRequest);
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String whiteListDomains = System.getenv(WHITE_LIST_DOMAINS);
        
        boolean acceptRequest = false;

        if (StringUtils.isNotBlank(whiteListDomains)) {
        	String currentOrigin = request.getHeader(ORIGIN);
        	long result = Arrays.asList(whiteListDomains.split(";"))
        	.stream()
        	.filter(s -> s.equalsIgnoreCase(currentOrigin))
        	.count();
        	
        	if (result > 0) {
        		
        		acceptRequest = true;
        	}
        	
        }else {
    		acceptRequest = true;

        }

        if (acceptRequest) {
        	response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, request.getHeader(ORIGIN));       	
        	response.addHeader(ACCESS_CONTROL_ALLOW_METHODS,"GET, OPTIONS, HEAD, PUT, POST, DELETE");
        	response.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, request.getHeader(ACCESS_CONTROL_REQUEST_HEADERS));

        	// For HTTP OPTIONS verb/method reply with ACCEPTED status code -- per CORS handshake
        	if (request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS)) {
        		response.setStatus(HttpServletResponse.SC_ACCEPTED);
        		return;
        	}
        }else {
    		response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
    		return;    	
        }
         
 
        // pass the request along the filter chain
        chain.doFilter(request, servletResponse);
    }
 

}
