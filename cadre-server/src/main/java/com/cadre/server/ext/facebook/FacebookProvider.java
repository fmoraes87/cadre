package com.cadre.server.ext.facebook;

import java.sql.Timestamp;
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
import com.restfb.DefaultFacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.FacebookException;
import com.restfb.types.User;

@CustomService(serviceId = IdentityProvider.class)
public class FacebookProvider implements IdentityProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(FacebookProvider.class);

	
	private static final String FB_ERROR_GETTING_DATA = "@FBErrorGettingData@";

	protected static final String FACEBOOK_PROVIDER = "facebook";

	private static final String FB_OBJECT_NAME_EMAIL = "name,email";
	private static final String FB_OBJECT_EMAIL = "email";

	private static final String FB_OBJECT_FIELDS = "fields";
	private static final String FB_OBJECT_ME = "me";
	private static final String APP_SECRET = "appSecret";

	@Override
	public IdentityResponse login(IdentityRequest loginRequest, String trxName) throws IdentityBrokerException {
		MServiceProvider facebookProvider = ServiceProviderBroker.getServiceProvider(FACEBOOK_PROVIDER);
		
		String accessToken = loginRequest.getParameter(ACCESS_TOKEN);
		String appSecret = facebookProvider.getAttributeValue(APP_SECRET);
		
		if (StringUtils.isNotEmpty(accessToken) && StringUtils.isNotEmpty(appSecret)) {
			try {
				DefaultFacebookClient facebookClient = new DefaultFacebookClient(accessToken,appSecret,Version.LATEST);
				User fbUser = facebookClient.fetchObject(FB_OBJECT_ME, User.class,Parameter.with(FB_OBJECT_FIELDS,FB_OBJECT_EMAIL));
				
				if (StringUtils.isNotEmpty(fbUser.getEmail())){
					ModelService modelService =  CDI.current().select(ModelService.class).get();
					
					SearchResult<MUser> columnsSearch = modelService.search(trxName,
							new JDBCQueryImpl.Builder(MUser.TABLE_NAME).and(GenericCondition.equals(MUser.COLUMNNAME_EMailUser, fbUser.getEmail())).build());

					MUser user = columnsSearch.getSingleResult(false);
					if (null==user) {
						user = createUser(trxName, fbUser.getEmail(), fbUser.getName());
						
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
					throw new IdentityBrokerException(Status.BAD_REQUEST.getStatusCode(),FB_ERROR_GETTING_DATA);
				}
				
			}catch(FacebookException fbx) {
				LOGGER.error("login()", fbx);

				throw new IdentityBrokerException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),FB_ERROR_GETTING_DATA);
			}
		}else {
			if (StringUtils.isEmpty(accessToken)) {
				throw new IdentityBrokerException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),ServiceProviderBroker.INVALID_SERVICE_PROVIDER_CONFIGURATION);				
			}else {
				throw new IdentityBrokerException(Status.BAD_REQUEST.getStatusCode(),FB_ERROR_GETTING_DATA);
			}
			
		}
		
	}

	@Override
	public void logout(String trxName) throws IdentityBrokerException {
		//Do nothing
	}

	@Override
	public MUser createUser(IdentityRequest createUserRequest, String trxName) throws IdentityBrokerException {
		MServiceProvider facebookProvider = ServiceProviderBroker.getServiceProvider(FACEBOOK_PROVIDER);
		
		String accessToken = createUserRequest.getParameter(ACCESS_TOKEN);
		String appSecret = facebookProvider.getAttributeValue(APP_SECRET);
		
		if (StringUtils.isNotEmpty(accessToken) && StringUtils.isNotEmpty(appSecret)) {
			try {
				DefaultFacebookClient facebookClient = new DefaultFacebookClient(accessToken,appSecret,Version.LATEST);
				User fbUser = facebookClient.fetchObject(FB_OBJECT_ME, User.class,Parameter.with(FB_OBJECT_FIELDS,FB_OBJECT_NAME_EMAIL));
				
				if (StringUtils.isNotEmpty(fbUser.getEmail())){
					ModelService modelService =  CDI.current().select(ModelService.class).get();

					SearchResult<MUser> columnsSearch = modelService.search(trxName,
							new JDBCQueryImpl.Builder(MUser.TABLE_NAME).and(GenericCondition.equals(MUser.COLUMNNAME_EMailUser, fbUser.getEmail())).build());

					MUser user = columnsSearch.getSingleResult(false);
					if (null==user) {
						return createUser(trxName, fbUser.getEmail(), fbUser.getName());
					}else {
						return user;
					}
					
				}else {
					throw new IdentityBrokerException(Status.BAD_REQUEST.getStatusCode(),FB_ERROR_GETTING_DATA);
				}
				
			}catch(FacebookException fbx) {
				LOGGER.error("createUser()", fbx);

				throw new IdentityBrokerException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),FB_ERROR_GETTING_DATA);
			}
		}else {
			if (StringUtils.isEmpty(accessToken)) {
				throw new IdentityBrokerException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),ServiceProviderBroker.INVALID_SERVICE_PROVIDER_CONFIGURATION);				
			}else {
				throw new IdentityBrokerException(Status.BAD_REQUEST.getStatusCode(),FB_ERROR_GETTING_DATA);
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
