package com.cadre.server.ext.google;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.annotation.CustomService;
import com.cadre.server.core.broker.ServiceProviderBroker;
import com.cadre.server.core.entity.MServiceProvider;
import com.cadre.server.core.entity.MUser;
import com.cadre.server.core.event.CEvent;
import com.cadre.server.core.event.CEventManager;
import com.cadre.server.core.event.EventTopics;
import com.cadre.server.core.persistence.query.GenericCondition;
import com.cadre.server.core.persistence.query.JDBCQueryImpl;
import com.cadre.server.core.persistence.query.SearchResult;
import com.cadre.server.core.security.IdentityBrokerException;
import com.cadre.server.core.security.IdentityProvider;
import com.cadre.server.core.service.ModelService;
import com.cadre.server.core.util.PasswordUtils;
import com.cadre.server.core.util.SecurityUtils;
import com.cadre.server.core.web.IdentityRequest;
import com.cadre.server.core.web.IdentityResponse;
import com.google.api.client.auth.openidconnect.IdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

@CustomService(serviceId = IdentityProvider.class)
public class GoogleProvider implements IdentityProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(GoogleProvider.class);

	private static final String GOOGLE_ERROR_GETTING_DATA = "@GoogleErrorGettingData@";
	protected static final String GOOGLE_PROVIDER = "google";
	protected static final String CLIENT_ID = "clientId";

	private static final HttpTransport transport = new NetHttpTransport();
	private static final JsonFactory jsonFactory = new JacksonFactory();

	@Override
	public IdentityResponse login(final IdentityRequest loginRequest, String trxName) throws IdentityBrokerException {

		MServiceProvider googleProvider = ServiceProviderBroker.getServiceProvider(GOOGLE_PROVIDER);

		String idTokenString = loginRequest.getParameter(ACCESS_TOKEN);
		String clientId = googleProvider.getAttributeValue(CLIENT_ID);

		if (StringUtils.isNotEmpty(idTokenString) && StringUtils.isNotEmpty(clientId)) {
			
			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
					.setAudience(Collections.singletonList(clientId))
					.build();
			
			try {
				GoogleIdToken idToken  = verifier.verify(idTokenString);
				if (idToken != null) {
					Payload payload = idToken.getPayload();
					String email = (String) payload.get("email");
					String name = (String) payload.get("name");

					ModelService modelService =  CDI.current().select(ModelService.class).get();
					
					SearchResult<MUser> columnsSearch = modelService.search(trxName,
							new JDBCQueryImpl.Builder(MUser.TABLE_NAME).and(GenericCondition.equals(MUser.COLUMNNAME_EMailUser, email)).build());

					MUser user = columnsSearch.getSingleResult(false);
					if (null==user) {
						user = createUser(trxName, email, name);
						
						Map<String,Object> requestParams = new HashMap<>();
						requestParams.putAll(loginRequest.getAllInfoAsMap());
						requestParams.put(CEventManager.EVENT_DATA, user);
						
						CEvent newUserEvent = CEventManager.newEvent(EventTopics.NEW_USER.name(),requestParams );
						CEventManager.getInstance().sendEvent(newUserEvent);
					}
					
					SecurityUtils.validateUser(trxName,user);

					user.setDateLastLogin(new Timestamp(System.currentTimeMillis()));
					modelService.save(user);
					try {
						final OAuthResponse oAuthResponse =  SecurityUtils.generateOAuthResponse(trxName,user);
						return convetToIdentityResponse(oAuthResponse);
						
					} catch (OAuthSystemException ex) {
						LOGGER.error("login()", ex);

						throw new IdentityBrokerException(Status.BAD_REQUEST.getStatusCode(),ex.getMessage());
					}
					
				}else {
					throw new IdentityBrokerException(Status.BAD_REQUEST.getStatusCode(),GOOGLE_ERROR_GETTING_DATA);
				}
			} catch (GeneralSecurityException | IOException  | java.lang.IllegalArgumentException e) {
				//java.lang.IllegalArgumentException : for invalid idToken
				LOGGER.error("login()", e);

				throw new IdentityBrokerException(Status.BAD_REQUEST.getStatusCode(),GOOGLE_ERROR_GETTING_DATA);
			}

		}else {
			if (StringUtils.isEmpty(clientId)) {
				throw new IdentityBrokerException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),ServiceProviderBroker.INVALID_SERVICE_PROVIDER_CONFIGURATION);				
			}else {
				throw new IdentityBrokerException(Status.BAD_REQUEST.getStatusCode(),GOOGLE_ERROR_GETTING_DATA);
			}
		}

	
	}

	@Override
	public void logout(String trxName) throws IdentityBrokerException {
		throw new IdentityBrokerException(Status.BAD_REQUEST.getStatusCode(),"@NotSupported@");
	}

	@Override
	public MUser createUser(IdentityRequest createUserRequest, String trxName) throws IdentityBrokerException {
		MServiceProvider googleProvider = ServiceProviderBroker.getServiceProvider(GOOGLE_PROVIDER);

		String idTokenString = createUserRequest.getParameter(ACCESS_TOKEN);
		String clientId = googleProvider.getAttributeValue(CLIENT_ID);

		if (StringUtils.isNotEmpty(idTokenString) && StringUtils.isNotEmpty(clientId)) {
			
			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
					.setAudience(Collections.singletonList(clientId))
					.build();
			
			try {
				GoogleIdToken idToken  = verifier.verify(idTokenString);
				if (idToken != null) {
					Payload payload = idToken.getPayload();
					String email = (String) payload.get("email");
					String name = (String) payload.get("name");
					
					ModelService modelService = CDI.current().select(ModelService.class).get();

					
					SearchResult<MUser> columnsSearch = modelService.search(trxName,
							new JDBCQueryImpl.Builder(MUser.TABLE_NAME).and(GenericCondition.equals(MUser.COLUMNNAME_EMailUser, email)).build());

					MUser user = columnsSearch.getSingleResult(false);
					if (null==user) {
						return createUser(trxName, email, name);
					}else {
						return user;
					}
				}else {
					throw new IdentityBrokerException(Status.BAD_REQUEST.getStatusCode(),GOOGLE_ERROR_GETTING_DATA);
				}
			} catch (GeneralSecurityException | IOException e) {
				LOGGER.error("createUser()", e);

				throw new IdentityBrokerException(Status.BAD_REQUEST.getStatusCode(),GOOGLE_ERROR_GETTING_DATA);
			}

		}else {
			if (StringUtils.isEmpty(clientId)) {
				throw new IdentityBrokerException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),ServiceProviderBroker.INVALID_SERVICE_PROVIDER_CONFIGURATION);				
			}else {
				throw new IdentityBrokerException(Status.BAD_REQUEST.getStatusCode(),GOOGLE_ERROR_GETTING_DATA);
			}
		}
	}
	
	private MUser createUser(String trxName, String email, String name) {
		ModelService modelService =  CDI.current().select(ModelService.class).get();

		MUser user = modelService.createPO(trxName, MUser.TABLE_NAME);
		user.setUserPIN(PasswordUtils.getRandomPass());
		user.setEMailUser(email.trim().toLowerCase());
		user.setIsActive(true);
		user.setName(StringUtils.isBlank(name)?email.trim().toLowerCase():name );
	
		user.setIsActive(true);
		user.setIsAccountVerified(true);

		modelService.save(user);
		
		return user;		
	}


}
