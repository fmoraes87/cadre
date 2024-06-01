package com.cadre.server.core.web.rest.filter;

import java.io.IOException;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.dto.RequestDataAccess;
import com.cadre.server.core.entity.DatabaseOperation;
import com.cadre.server.core.entity.ResourceType;
import com.cadre.server.core.resolver.DynamicServiceResolver;
import com.cadre.server.core.service.DataAccessService;
import com.cadre.server.core.util.SecurityUtils;

public class BearerTokenFilter implements ContainerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(BearerTokenFilter.class);

	private DataAccessService accessService;
	private DatabaseOperation operation;

	public BearerTokenFilter(DatabaseOperation operation) {
		this.operation = operation;
		this.accessService =  DynamicServiceResolver.locate(DataAccessService.class);

	}
	
	@Override
	public void filter(ContainerRequestContext request) throws IOException {
		try {
			String token = StringUtils.defaultIfEmpty(
					request.getHeaderString(HttpHeaders.AUTHORIZATION),
					request.getUriInfo().getQueryParameters().getFirst("tokenField"));
			
			String path = request.getUriInfo().getPath();
			if (token == null) {
				throw new NotAuthorizedException(OAuthError.CodeResponse.ACCESS_DENIED);
			}
			
			SecurityUtils.loadUserInfoFrom(token);
			
			RequestDataAccess requestAccess = new RequestDataAccess( ResourceType.ENDPOINT, path, operation);
			
			if (!accessService.isClientAllowed(null, requestAccess)) {
		         throw new NotAuthorizedException(OAuthError.CodeResponse.ACCESS_DENIED);
			}
			
		} catch (OAuthSystemException ex) {
			LOGGER.error("Checking User Authorization", ex);
	         throw new NotAuthorizedException(ex.getMessage());
		}

	}

}
