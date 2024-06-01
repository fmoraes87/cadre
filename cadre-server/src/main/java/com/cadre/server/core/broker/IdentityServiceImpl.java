package com.cadre.server.core.broker;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.json.JsonException;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.entity.MOAuth2Client;
import com.cadre.server.core.entity.MServiceProvider;
import com.cadre.server.core.entity.MUser;
import com.cadre.server.core.event.CEvent;
import com.cadre.server.core.event.CEventManager;
import com.cadre.server.core.event.EventTopics;
import com.cadre.server.core.exception.CadreException;
import com.cadre.server.core.persistence.query.GenericCondition;
import com.cadre.server.core.persistence.query.JDBCQueryImpl;
import com.cadre.server.core.persistence.query.SearchResult;
import com.cadre.server.core.resolver.DynamicServiceResolver;
import com.cadre.server.core.resolver.ResolverQuery;
import com.cadre.server.core.resolver.ServiceType;
import com.cadre.server.core.security.IdentityBrokerException;
import com.cadre.server.core.security.IdentityProvider;
import com.cadre.server.core.service.ModelService;
import com.cadre.server.core.util.BaseUtil;
import com.cadre.server.core.util.JSONUtils;
import com.cadre.server.core.util.SecurityUtils;
import com.cadre.server.core.web.IdentityRequest;
import com.cadre.server.core.web.IdentityResponse;

@Singleton
public class IdentityServiceImpl implements IdentityService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IdentityServiceImpl.class);

	
	public static final String TEMPLATE_REGISTER = "register"; 
	public static final String TEMPLATE_FORGOT_PASSWORD = "forgotPassword"; 

	private static final String ERROR_NOT_ALLOWED = "@NotAllowed@";
	
	@Inject
	private NotificationBroker notificationService;
	
	@Inject
	private ModelService modelService;

	public IdentityServiceImpl() {}
	
	
	@Override
	public IdentityResponse login(IdentityRequest loginRequest,String trxName) throws IdentityBrokerException {

		CEvent loginRequestEvent = CEventManager.newEvent(EventTopics.PO_LOGIN_REQUEST.name(), loginRequest.getAllInfoAsMap());
		CEventManager.getInstance().postEvent(loginRequestEvent);
		
		ResolverQuery query = new ResolverQuery();
		query.put(MServiceProvider.COLUMNNAME_Value, loginRequest.getLoginType());	
		query.put(MServiceProvider.COLUMNNAME_ServiceType, ServiceType.IDENTITY_PROVIDER);	

		IdentityProvider identityProvider = DynamicServiceResolver.locate(IdentityProvider.class, query);

		IdentityResponse response = identityProvider.login(loginRequest,trxName);

		CEvent loginResponseEvent = CEventManager.newEvent(EventTopics.PO_LOGIN_RESPONSE.name(), response.getBody());
		CEventManager.getInstance().postEvent(loginResponseEvent);

		return response;

	}

	@Override
	public MUser createUser(IdentityRequest request,String trxName) {
		
		ResolverQuery query = new ResolverQuery();
		query.put(MServiceProvider.COLUMNNAME_Value, request.getLoginType());	
		query.put(MServiceProvider.COLUMNNAME_ServiceType, ServiceType.IDENTITY_PROVIDER);	
		
		IdentityProvider identityProvider = DynamicServiceResolver.locate(IdentityProvider.class, query);
		MUser newUser = identityProvider.createUser(request,trxName);
		
		Map<String,Object> requestParams = new HashMap<>();
		requestParams.putAll(request.getAllInfoAsMap());
		requestParams.put(CEventManager.EVENT_DATA, newUser);
		
		CEvent newUserEvent = CEventManager.newEvent(EventTopics.NEW_USER.name(),requestParams );
		CEventManager.getInstance().sendEvent(newUserEvent);
		
		return newUser;
	}
	

	
	
	@Override
	public MUser confirmUserAccount(String trxName,String trxCode) {
		try {
			byte [] decodedTrxCode = Base64.getDecoder().decode(trxCode);
			String secretKey = SysConfigServiceBroker.getValue(SecurityUtils.SECRET_KEY,SecurityUtils.DEFAULT_SECRET_KEY);
			Map<String,Object> values = JSONUtils.getKeyValuesAsMap(SecurityUtils.decrypt(secretKey,decodedTrxCode));
			MUser currentUser = modelService.getPO(trxName, MUser.TABLE_NAME, MUser.COLUMNNAME_AD_User_UU, values.get(MUser.COLUMNNAME_AD_User_UU));
			if (!currentUser.isAccountVerified()) {
				currentUser.setIsAccountVerified(true);
				modelService.save(currentUser);
			}else {
				throw new CadreException(Status.BAD_REQUEST.getStatusCode(), ERROR_NOT_ALLOWED );
			}
			
			return currentUser;
			
		}catch(IllegalArgumentException ex) {
			throw new CadreException(Status.BAD_REQUEST.getStatusCode(), ERROR_NOT_ALLOWED );
		}

	}
	
	@Override
	public void updateUserPassword(String trxName,String trxCode, String newUserPin) {
		try {
			String secretKey = SysConfigServiceBroker.getValue(SecurityUtils.SECRET_KEY,SecurityUtils.DEFAULT_SECRET_KEY);
			
			updatePassword(trxName, trxCode, newUserPin, secretKey);
			
		}catch (JsonException ex) {
			LOGGER.error("updateUserPassword ("+trxCode+")" ,ex);
			throw new CadreException(Status.BAD_REQUEST.getStatusCode(),"@BadRequest@");
		}
		
				
	}


	private void updatePassword(String trxName, String trxCode, String newUserPin, String secretKey) {
		byte [] decodedTrxCode = Base64.getDecoder().decode(trxCode);
		Map<String,Object> values = JSONUtils.getKeyValuesAsMap(SecurityUtils.decrypt(secretKey,decodedTrxCode));
		
		SearchResult<MUser> currentUserSearch = modelService.search(trxName,
				new JDBCQueryImpl.Builder(MUser.TABLE_NAME)
				.and(GenericCondition.equals(MUser.COLUMNNAME_AD_User_UU,values.get(MUser.COLUMNNAME_AD_User_UU)))
				.and(GenericCondition.equals(MUser.COLUMNNAME_UserPIN,values.get(MUser.COLUMNNAME_UserPIN)))
				.build());
		
		MUser currentUser = currentUserSearch.getSingleResult(false);
		if (null==currentUser) {
			throw new CadreException(Status.FORBIDDEN.getStatusCode(),"@Unauthorized@");

		}
		currentUser.setUserPIN(newUserPin);
		modelService.save(currentUser);
	}

	@Override
	public void resetPassword(String email) {
		try {
			MUser user = modelService.getPO(null, MUser.TABLE_NAME, MUser.COLUMNNAME_EMailUser, email);

			String secretKey = SysConfigServiceBroker.getValue(SecurityUtils.SECRET_KEY,SecurityUtils.DEFAULT_SECRET_KEY);

			Map<String, Object> model = new HashMap<String, Object>();
			model.put("name", user.getName());
			model.put("url", BaseUtil.getURLToUpdatePassword(secretKey,user));

			notificationService.sendNotificationlWithTemplate(
					NotificationBroker.NOTIFICATION_TYPE_EMAIL,
					user.getEMailUser(), // to
					TEMPLATE_FORGOT_PASSWORD, 
			        model// values
				); 
			
		}catch(CadreException ex) {
			LOGGER.error("resetPassword ("+email+")" ,ex);
		}
		
	}


	@Override
	public void updateMyPassword(String trxName, String trxCode, String newUserPin) {

		MOAuth2Client oAuthClient = modelService.getPO(trxName, MOAuth2Client.TABLE_NAME,CadreEnv.getAD_OAuth2_Client_ID());
		updatePassword(trxName, trxCode, newUserPin, oAuthClient.getClientSecret());

	}


	@Override
	public void sendConfirmUserPassword(MUser user) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("name", user.getName());
		
		String secretKey = SysConfigServiceBroker.getValue(SecurityUtils.SECRET_KEY,SecurityUtils.DEFAULT_SECRET_KEY);

		model.put("url", BaseUtil.getURLToConfirmAccount(secretKey,user));

		NotificationBroker notificationService = CDI.current().select(NotificationBroker.class).get();
		notificationService.sendNotificationlWithTemplate(
					NotificationBroker.NOTIFICATION_TYPE_EMAIL,
					user.getEMailUser(), // to
					IdentityServiceImpl.TEMPLATE_REGISTER, 
			        model// values
				); 
		
	}
	

}
