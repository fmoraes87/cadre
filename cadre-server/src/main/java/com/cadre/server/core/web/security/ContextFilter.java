package com.cadre.server.core.web.security;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.CadreEnv;

@WebFilter(value = { "/*" })
public class ContextFilter implements Filter {


	private static final Logger LOGGER = LoggerFactory.getLogger(ContextFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		LOGGER.info("Initializing {}", ContextFilter.class);

	}

	
	/**
	 * Constants
	 */
	public static final String HEADER_LANGUAGE = "P-Language";

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {

		try {
			// initialize context
			CadreEnv.setCtx(new Properties());

			if (servletRequest instanceof HttpServletRequest) {
				HttpServletRequest req = (HttpServletRequest) servletRequest;
				CadreEnv.setContextValue(CadreEnv.LANGUAGE, req.getHeader(HEADER_LANGUAGE));

			}

			// pass the request along the filter chain
			chain.doFilter(servletRequest, servletResponse);

		} finally {

			CadreEnv.clean();
		}

	}

}
