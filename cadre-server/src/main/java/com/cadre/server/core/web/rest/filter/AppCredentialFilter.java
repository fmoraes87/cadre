package com.cadre.server.core.web.rest.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.dto.RequestDataAccess;
import com.cadre.server.core.entity.DatabaseOperation;
import com.cadre.server.core.entity.ResourceType;
import com.cadre.server.core.resolver.DynamicServiceResolver;
import com.cadre.server.core.service.DataAccessService;
import com.cadre.server.core.util.SecurityUtils;

public class AppCredentialFilter implements ContainerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppCredentialFilter.class);

	
    @Context
    private HttpServletRequest servletRequest;
    
	private DataAccessService accessService;
	private DatabaseOperation operation;

	public AppCredentialFilter(DatabaseOperation operation) {
		this.operation = operation;
		this.accessService =  DynamicServiceResolver.locate(DataAccessService.class);

	}
	
	@Override
	public void filter(ContainerRequestContext request) throws IOException {
		try {
			String path = request.getUriInfo().getPath();
			
			String[] credentials =  OAuthUtils.decodeClientAuthenticationHeader(
					 StringUtils.defaultIfEmpty(
								request.getHeaderString(HttpHeaders.AUTHORIZATION),
								request.getUriInfo().getQueryParameters().getFirst("tokenField"))
					);
			
			if (credentials!=null && credentials.length > 1) {
				SecurityUtils.validateClient(null,credentials[0], credentials[1]);
				
				RequestDataAccess requestAccess = new RequestDataAccess(ResourceType.ENDPOINT, path, operation);
				
				if (!accessService.isClientAllowed(null, requestAccess)) {
					throw new NotAuthorizedException(OAuthError.CodeResponse.ACCESS_DENIED);
				}
				
			}else {
				throw new NotAuthorizedException(OAuthError.CodeResponse.INVALID_REQUEST);
			}
		} catch (OAuthSystemException ex) {
			LOGGER.error("Checking Client Authorization", ex);

	         throw new NotAuthorizedException(ex.getMessage());
		}

	}

}
