package com.cadre.server.core.web.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter(value = { "/*" })
public class HttpsEnforcer implements Filter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpsEnforcer.class);

	  public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";

	  @Override
	  public void init(FilterConfig filterConfig) throws ServletException {
	        LOGGER.info("Initializing {}", HttpsEnforcer.class);

	  }

	  @Override
	  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
	    HttpServletRequest request = (HttpServletRequest) servletRequest;
	    HttpServletResponse response = (HttpServletResponse) servletResponse;

	    if (request.getHeader(X_FORWARDED_PROTO) != null) {
	      if (request.getHeader(X_FORWARDED_PROTO).indexOf("https") != 0) {
	    	  
	    	LOGGER.info("Sending redirect to https");
	    	
	        //instruct the browser to always use the HTTPS version in future
	        response.setHeader("Strict-Transport-Security", "max-age=31536000");
	    	  
	        String pathInfo = (request.getPathInfo() != null) ? request.getPathInfo() : "";
	        response.sendRedirect("https://" + request.getServerName() + pathInfo);
	        return;
	      }
	    }

	    filterChain.doFilter(request, response);
	  }

	  @Override
	  public void destroy() { }
	}
