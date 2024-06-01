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

@WebFilter(value = {"/*"})
public class SecurityHeaderFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityHeaderFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		LOGGER.info("Initializing {}", SecurityHeaderFilter.class);

	}

	/**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
 
        HttpServletRequest request = ((HttpServletRequest) servletRequest);
        HttpServletResponse response = (HttpServletResponse) servletResponse;
 
        // pass the request along the filter chain
        chain.doFilter(request, servletResponse);
        
        //Set to nosniff to prevent the browser guessing the correct Content-Type
        response.setHeader("X-Content-Type-Options", "nosniff");
        //Set to DENY to prevent your API responses being  loaded in a frame or iframe.
        response.setHeader("X-Frame-Options", "DENY");
        //The current guidance is to set to “0” on API responses to completely disable these protections
        response.setHeader("X-XSS-Protection", "0");
        //. The safest default is  to disable caching completely using the no-store
        response.setHeader("Cache-Control", "no-store");
        
        //Recommended CSP directives for REST responses
        // default-src - none Prevents the response from loading any scripts or resources
        //frame-ancestors - none -  this prevents the response being loaded into an iframe.
        //sandbox n/a - Disables scripts and other potentially dangerous content from being executed.
        response.setHeader("Content-Security-Policy", "default-src 'none'; frame-ancestors 'none'; sandbox");
        response.setHeader("Server", "");
    }
 

}
