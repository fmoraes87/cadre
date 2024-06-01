package com.cadre.server.core.security.impl;

import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.core.Response.Status;

import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.annotation.CustomService;
import com.cadre.server.core.entity.MUser;
import com.cadre.server.core.resolver.DynamicServiceResolver;
import com.cadre.server.core.security.IdentityBrokerException;
import com.cadre.server.core.security.IdentityProvider;
import com.cadre.server.core.service.ModelService;
import com.cadre.server.core.service.OAuthService;
import com.cadre.server.core.web.IdentityRequest;
import com.cadre.server.core.web.IdentityResponse;

@CustomService(serviceId = IdentityProvider.class )
public class UsernamePasswordLoginRealm implements IdentityProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UsernamePasswordLoginRealm.class);

	
	public UsernamePasswordLoginRealm() {}

	@Override
	public IdentityResponse login(final IdentityRequest loginRequest, String trxName) throws IdentityBrokerException {

		try {
			final OAuthService oAuthService = DynamicServiceResolver.locate(OAuthService.class);
			final OAuthTokenRequest oauthRequest = new OAuthTokenRequest(loginRequest);
			final OAuthResponse oAuthResponse = oAuthService.processOAuthTokenRequest(oauthRequest);
			
			return convetToIdentityResponse(oAuthResponse);
			
		} catch (OAuthSystemException | OAuthProblemException ex) {
			LOGGER.error("Loggin failed", ex);

			throw new IdentityBrokerException(Status.FORBIDDEN.getStatusCode(),ex.getMessage());
		}

	}

	@Override
	public void logout(String trxName) throws IdentityBrokerException {
		throw new IdentityBrokerException(Status.NOT_ACCEPTABLE.getStatusCode(),"@NotSupported@");
	}

	@Override
	public MUser createUser(IdentityRequest createUserRequest, String trxName) throws IdentityBrokerException {
		
		ModelService modelService =  CDI.current().select(ModelService.class).get();

		MUser user = modelService.createPO(trxName, MUser.TABLE_NAME);
		user.setUserPIN(createUserRequest.getParameter(MUser.COLUMNNAME_UserPIN));
		user.setEMailUser(createUserRequest.getParameter(MUser.COLUMNNAME_EMailUser).trim().toLowerCase());
		user.setName(createUserRequest.getParameter(MUser.COLUMNNAME_Name).trim());		
	
		user.setIsActive(true);
		user.setIsAccountVerified(false);

		modelService.save(user);
		
		return user;
	}


}
