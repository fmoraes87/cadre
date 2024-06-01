package com.cadre.server.core.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.inject.spi.CDI;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.broker.IdentityService;
import com.cadre.server.core.entity.AccessLevel;
import com.cadre.server.core.entity.MAppRule;
import com.cadre.server.core.entity.MOAuth2Client;
import com.cadre.server.core.entity.MOAuth2ClientToken;
import com.cadre.server.core.entity.MUser;
import com.cadre.server.core.entity.MUserApp;
import com.cadre.server.core.entity.POInfo;
import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.exception.CadreException;
import com.cadre.server.core.persistence.jdbc.RDBMS;
import com.cadre.server.core.persistence.query.FlexibleSearchQuery;
import com.cadre.server.core.persistence.query.GenericCondition;
import com.cadre.server.core.persistence.query.JDBCQueryImpl;
import com.cadre.server.core.persistence.query.SearchQuery;
import com.cadre.server.core.persistence.query.SearchResult;
import com.cadre.server.core.resolver.DynamicServiceResolver;
import com.cadre.server.core.security.oauth2.OAuthException;
import com.cadre.server.core.security.oauth2.OAuthMessageType;
import com.cadre.server.core.service.ModelService;
import com.cadre.server.core.service.OAuthService;

public class SecurityUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityUtils.class);


	private static final String BLOWFISH_ALGORITHM = "Blowfish";

	public final static String AUTHORIZATION = "Authorization";

	private static final String BEARER_TYPE = "Bearer ";
	
	private static final String SQL_GET_AD_CLIENT_FROM_OAUTH_CLIENT= "SELECT AD_Client_ID FROM AD_OAuth2_Client WHERE ClientId=?";

	
	public static final String SECRET_KEY = "SECRET_KEY";
	public static final String DEFAULT_SECRET_KEY="MyPersonalKey@10283012831";
	
	public static final String GRANT_TYPE = "password";
	public static final String GRANT_TYPE_PASSWORD = "password";
	public static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
	public static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
	public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";


	public static void loadUserInfoFrom(String token) throws OAuthSystemException {

		OAuthService oAuthService = DynamicServiceResolver.locate(OAuthService.class);
		oAuthService.parseToken(getTokenFromHTTPRequest(token));


	}

	/**
	 * Get token from http request.
	 * 
	 * @param headers
	 * @return
	 * @throws OAuthException
	 */
	private static String getTokenFromHTTPRequest(String token) throws OAuthException {

		if (StringUtils.isNotEmpty(token)) {
			return token.replaceAll(BEARER_TYPE, StringUtils.EMPTY);
		}

		return StringUtils.EMPTY;
	}

	/**
	 * Validate Token From
	 * 
	 * @param req
	 * @throws OAuthException
	 */
	public static void validateTokenFrom(HttpServletRequest req) throws OAuthSystemException {
		loadUserInfoFrom(req.getHeader(AUTHORIZATION));

	}

	public static MOAuth2Client validateClient(final String trxName, final String clientId, final String clientSecret)
			throws OAuthException {

		try {
			ModelService genericService = CDI.current().select(ModelService.class).get();
			
			//Update ClientId
			int currentClientID = RDBMS.getSQLValueEx(trxName, SQL_GET_AD_CLIENT_FROM_OAUTH_CLIENT,clientId);
			
			CadreEnv.setContextValue(CadreEnv.AD_CLIENT_ID, currentClientID);
			

			SearchQuery query = new JDBCQueryImpl.Builder(MOAuth2Client.TABLE_NAME)
					.and(GenericCondition.equals(MOAuth2Client.COLUMNNAME_ClientID, clientId))
					.and(GenericCondition.equals(MOAuth2Client.COLUMNNAME_ClientSecret, clientSecret))
					.build();

			SearchResult<MOAuth2Client> result = genericService.search(trxName, query);
			final MOAuth2Client oauthClient = result.getSingleResult();
			if (!oauthClient.isValid()) {
				throw new OAuthException(OAuthMessageType.ERROR_OAUTHCLIENT_INVALID);
			}

			CadreEnv.setContextValue(CadreEnv.AD_CLIENT_ID, oauthClient.getAD_Client_ID());
			CadreEnv.setContextValue(CadreEnv.AD_ORG_ID, oauthClient.getAD_Org_ID());
			CadreEnv.setContextValue(CadreEnv.AD_OAuth2_Client_ID, oauthClient.getAD_OAuth2_Client_ID());
			CadreEnv.setContextValue(CadreEnv.AD_App_ID, oauthClient.getAD_App_ID());

			CadreEnv.setContextValue(SecurityUtils.GRANT_TYPE, SecurityUtils.GRANT_TYPE_CLIENT_CREDENTIALS);

			
			MUser user =genericService.getPO(trxName, MUser.TABLE_NAME,oauthClient.getAD_User_ID());
			
			CadreEnv.setContextValue(CadreEnv.AD_USER_ID, user.getAD_User_ID());
			CadreEnv.setContextValue(CadreEnv.AD_USER_ACCESS_LEVEL, user.getUserLevel());
			CadreEnv.setContextValue(CadreEnv.ONLY_ACTIVE_RECORDS, user.isViewOnlyActiveRecords());

			return oauthClient;

		} catch (CadreException ex) {
			LOGGER.error("validateClient(clientId="+ clientId+")=> INVALID_CLIENT", ex);

			throw new OAuthException(OAuthError.TokenResponse.INVALID_CLIENT);
		}
	}

	public static OAuthResponse generateOAuthResponse(final String trxName, final MUser user) throws OAuthSystemException {
		ModelService modelService = CDI.current().select(ModelService.class).get();

		MOAuth2Client oAuthClient = modelService.getPO(trxName, MOAuth2Client.TABLE_NAME,
				CadreEnv.getAD_OAuth2_Client_ID());
		final OAuthService oAuthService = DynamicServiceResolver.locate(OAuthService.class);
		
		MOAuth2ClientToken token = oAuthService.generateOAuthToken(trxName, oAuthClient, user);

		return oAuthService.generateTokenResponse(user,oAuthClient, token);

	}
	
	public static void validateUser(String trxName, MUser user) {
		if (user != null && user.isValid()) {
			
			if (!user.isAccountVerified() /*&& needVerifyAccount(user)*/) {
				IdentityService identityBroker = CDI.current().select(IdentityService.class).get();
				identityBroker.sendConfirmUserPassword(user);
				
				throw new CadreException(Status.FORBIDDEN.getStatusCode(),"@AccountNotVerified@");
			}else {
				SearchQuery queryUserApp = new JDBCQueryImpl.Builder(MUserApp.TABLE_NAME)
						.and(GenericCondition.equals(MUserApp.COLUMNNAME_AD_User_ID, user.getAD_User_ID()))
						.and(GenericCondition.equals(MUserApp.COLUMNNAME_AD_App_ID,CadreEnv.get_AD_App_ID()))
						.and(GenericCondition.equals(MUserApp.COLUMNNAME_IsActive,"Y"))
						.build();
				
				ModelService genericService = CDI.current().select(ModelService.class).get();
				SearchResult<MUserApp> userApps = genericService.search(trxName, queryUserApp);
				if (CollectionUtils.isEmpty(userApps.getResultList(false))) {
					throw new CadreException(Status.FORBIDDEN.getStatusCode(),OAuthError.CodeResponse.ACCESS_DENIED);
				}
				
				CadreEnv.setContextValue(CadreEnv.AD_USER_ID, user.getAD_User_ID());
				CadreEnv.setContextValue(CadreEnv.AD_USER_ACCESS_LEVEL, user.getUserLevel());
				CadreEnv.setContextValue(SecurityUtils.GRANT_TYPE, SecurityUtils.GRANT_TYPE_PASSWORD);
				CadreEnv.setContextValue(CadreEnv.ONLY_ACTIVE_RECORDS, user.isViewOnlyActiveRecords());

			}

		} else {
			throw new CadreException(Status.FORBIDDEN.getStatusCode(),OAuthError.CodeResponse.UNAUTHORIZED_CLIENT);
		}
	}

	/**
	 * 
	 * @param user
	 * @return
	 */
	/*public static boolean needVerifyAccount(MUser user) {
		return DateUtils.addDays(user.getCreated(), 2).compareTo(LocalDateTime.now().toDate()) < 0;
	}*/

	public static byte [] encripty(String strkey,String to_encrypt) {
		try {
			SecretKeySpec key = new SecretKeySpec(strkey.getBytes(java.nio.charset.StandardCharsets.UTF_8), BLOWFISH_ALGORITHM);
			Cipher cipher = Cipher.getInstance(BLOWFISH_ALGORITHM);
			if ( cipher == null || key == null) {
				throw new CadreException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),OAuthError.CodeResponse.SERVER_ERROR);
			}
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(to_encrypt.getBytes(java.nio.charset.StandardCharsets.UTF_8));
			
			
		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			LOGGER.error("encripty(strkey="+ strkey+",to_encrypt="+ to_encrypt+")", e);
			throw new CadreException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),OAuthError.CodeResponse.SERVER_ERROR);
		}
	}

	public static String decrypt(String strkey, byte [] encryptedData) {
		try {

			SecretKeySpec key = new SecretKeySpec(strkey.getBytes(java.nio.charset.StandardCharsets.UTF_8), BLOWFISH_ALGORITHM);
			Cipher cipher = Cipher.getInstance(BLOWFISH_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decrypted = cipher.doFinal(encryptedData);
			return new String(decrypted);

		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException
				| NoSuchAlgorithmException | NoSuchPaddingException e) {
			
			LOGGER.error("decrypt(strkey="+ strkey+")", e);
			throw new CadreException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),OAuthError.CodeResponse.SERVER_ERROR);
		}
	}

	public static boolean canUpdate(POModel poModel) {
		
		if (SecurityUtils.canView(poModel)) {
			
			boolean	retValue = true;

			String userLevel = CadreEnv.getContext(CadreEnv.getCtx(), CadreEnv.AD_USER_ACCESS_LEVEL);
			int p_AD_Client_ID = poModel.getAD_Client_ID();
			int p_AD_Org_ID = poModel.getAD_Org_ID();
			
			//doesnt allow  the user create/update other client data
			if (p_AD_Client_ID!=CadreEnv.getAD_Client_ID()) {
				retValue = false;
			}
			
			//	System == Client=0 & Org=0
			if (p_AD_Client_ID == 0 && p_AD_Org_ID == 0 && userLevel.charAt(0) != 'S')
			{
				retValue = false;
			}

			//	Client == Client!=0 & Org=0 
			else if (p_AD_Client_ID != 0 && p_AD_Org_ID == 0
				&& userLevel.charAt(1) != 'C')
			{
				if (userLevel.charAt(2) == 'O')
					;	//	Client+Org with access to *
				else
				{
					retValue = false;
				}
			}

			//	Organization == Client!=0 & Org!=0
			else if (p_AD_Client_ID != 0 && p_AD_Org_ID != 0
				&& userLevel.charAt(2) != 'O')
			{
				retValue = false;
			}
			
			return retValue;
					
		}
		return false;
		
	}

	public static boolean canView(POModel poModel) {
		boolean retValue = true;

		POInfo poInfo = poModel.getPOInfo();
		String userLevel = CadreEnv.getContext(CadreEnv.getCtx(), CadreEnv.AD_USER_ACCESS_LEVEL);
		
		int v_AD_Client_ID = poModel.getAD_Client_ID();
		int v_AD_Org_ID = poModel.getAD_Org_ID();

		// 7 - All
		if (AccessLevel.ACCESSLEVEL_All.equals(poInfo.getAccessLevel()))
			retValue = true;

		// 4 - System data requires S
		else if (AccessLevel.ACCESSLEVEL_SystemOnly.equals(poInfo.getAccessLevel())	&& userLevel.charAt(0) != 'S')
			retValue = false;

		// 2 - Client data requires C
		else if (AccessLevel.ACCESSLEVEL_ClientOnly.equals(poInfo.getAccessLevel()) && userLevel.charAt(1) != 'C') {
			
			if (userLevel.charAt(2) == 'O' && CadreEnv.getAD_Org_ID()== v_AD_Org_ID) {
				;//	Client+Org with access to *
			}else {
				retValue = false;				
			}
		}

		// 1 - Organization data requires O
		else if (AccessLevel.ACCESSLEVEL_Organization.equals(poInfo.getAccessLevel()) || (v_AD_Client_ID != 0 && v_AD_Org_ID != 0)
				&& userLevel.charAt(2) != 'O')
			retValue = false;

		// 3 - Client Shared requires C or O
		else if (AccessLevel.ACCESSLEVEL_ClientPlusOrganization.equals(poInfo.getAccessLevel())
				&& (!(userLevel.charAt(1) == 'C' || userLevel.charAt(2) == 'O')))
			retValue = false;

		// 6 - System/Client requires S or C
		else if (AccessLevel.ACCESSLEVEL_SystemPlusClient.equals(poInfo.getAccessLevel())
				&& (!(userLevel.charAt(0) == 'S' || userLevel.charAt(1) == 'C')))
			retValue = false;
		
		/*if (retValue) {
			retValue = isOrgAccess(v_AD_Org_ID);
		}*/
		
		return retValue;
		
	}

	public static String getMenuAccessSQL() {
		
		String sql = " AND (AD_TreeNode.AD_Window_ID is Null OR EXISTS (SELECT 1 as x"
		+ " 	FROM AD_User_Roles ur"
		+ "  INNER JOIN AD_Role r on r.ad_role_id= ur.ad_role_id and r.IsActive='Y' "
		+ "  INNER JOIN AD_Window_Access oa on oa.AD_Role_ID = r.AD_Role_ID "
		+ "					and oa.IsActive='Y' "
		+ " WHERE ur.AD_User_ID= " + CadreEnv.getAD_User_ID()
		+ "			and ur.IsActive='Y' "
		+ "			and ur.AD_Client_ID in(0," + CadreEnv.getAD_Client_ID()+ ")"
		+ "			and ur.AD_Org_ID in(0," + CadreEnv.getAD_Org_ID()+ ")"
		+ "			and oa.AD_Window_ID=AD_TreeNode.AD_Window_ID "
		+ " UNION "
		+ " SELECT 1 as x"
		+ " FROM AD_User  u"
		+ " WHERE u.AD_User_ID= " + CadreEnv.getAD_User_ID()
		+ "		AND u.isAdmin='Y' )) ";
		
		return sql.toString();
	}

	public static String getDynamicRules(String tablename) {
		int p_AD_Table_ID = RDBMS.
				getSQLValueEx(CadreEnv.getTrxName(), "SELECT AD_Table_ID FROM AD_Table WHERE TableName=?", tablename);
		
		if (p_AD_Table_ID > 0) {
			ModelService genericService = CDI.current().select(ModelService.class).get();
			
			final FlexibleSearchQuery flxQuery = new FlexibleSearchQuery(MAppRule.TABLE_NAME);
			
			StringBuilder searchWhereClause = new StringBuilder();
					
			searchWhereClause.append(MAppRule.COLUMNNAME_AD_App_ID+ "=" +  CadreEnv.get_AD_App_ID());
			searchWhereClause.append(" AND " +MAppRule.COLUMNNAME_AD_Table_ID+ "=" + p_AD_Table_ID);
			searchWhereClause.append(" AND " + MAppRule.COLUMNNAME_IsActive+ "= '" + POModel.YES_VALUE + "' ");
			searchWhereClause.append(" AND ( ");
			searchWhereClause.append(MAppRule.COLUMNNAME_AD_Role_ID+ " is null or ");
			searchWhereClause.append(MAppRule.COLUMNNAME_AD_Role_ID
					+ " in (select ad_role_id from cadre.AD_User_Roles ur where ur.isActive='Y' and ur.ad_user_id= " + CadreEnv.getAD_User_ID()+")");
			searchWhereClause.append(" )");
			
			flxQuery.setWhereClause(searchWhereClause.toString());

	
			SearchResult<MAppRule> result = genericService.search(CadreEnv.getTrxName(), flxQuery);
			List<MAppRule> list = result.getResultList(false);
			
			if (CollectionUtils.isNotEmpty(list)) {
				StringBuilder whereClause = new StringBuilder();
						
				list.stream().forEach( el -> {
					if (StringUtils.isNotBlank(el.getExpression())) {
						whereClause.append(" AND (" + ParserUtil.getFilterCode(CadreEnv.getCtx(),el.getExpression() , el.get_TrxName())+") ");
					}
				});
				
				return whereClause.toString();
				
			}else {
				return StringUtils.EMPTY;
				
			}
		}else {
			return StringUtils.EMPTY;
		}
		

	}


}
