package com.cadre.server.core.service.impl;

import java.sql.Timestamp;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.as.response.OAuthASResponse.OAuthTokenResponseBuilder;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.annotation.CustomService;
import com.cadre.server.core.cache.CCache;
import com.cadre.server.core.entity.MClient;
import com.cadre.server.core.entity.MOAuth2Client;
import com.cadre.server.core.entity.MOAuth2ClientToken;
import com.cadre.server.core.entity.MUser;
import com.cadre.server.core.exception.CadreException;
import com.cadre.server.core.persistence.jdbc.RDBMS;
import com.cadre.server.core.persistence.jdbc.Trx;
import com.cadre.server.core.persistence.query.GenericCondition;
import com.cadre.server.core.persistence.query.JDBCQueryImpl;
import com.cadre.server.core.persistence.query.SearchQuery;
import com.cadre.server.core.persistence.query.SearchResult;
import com.cadre.server.core.security.oauth2.OAuthException;
import com.cadre.server.core.service.ModelService;
import com.cadre.server.core.service.OAuthService;
import com.cadre.server.core.util.PasswordUtils;
import com.cadre.server.core.util.SecurityUtils;

@CustomService(serviceId = OAuthService.class)
public class OAuth2ServiceImpl implements OAuthService{
	
	private static final String SQL_INVALIDATE_OLD_TOKENS = "UPDATE AD_OAuth2_Client_Token SET IsActive='N' WHERE AD_User_ID=? and AD_OAuth2_Client_ID=?  ";


	private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2ServiceImpl.class);


	private static final String SQL_GET_AD_CLIENT_FROM_ACCESS_TOKEN = "SELECT AD_Client_ID FROM AD_OAuth2_Client_Token WHERE AccessToken=?";
	private static final String SQL_GET_AD_CLIENT_FROM_AD_USER = "SELECT AD_Client_ID FROM AD_User WHERE lower(EMailUser)=lower(?)";
	private static final String SQL_GET_AD_CLIENT_FROM_REFRESH_TOKEN = "SELECT AD_Client_ID FROM AD_OAuth2_Client_Token WHERE RefreshToken=?";
	//private static final String SQL_GET_AD_USER_CAN_DO_A_LOGIN = "SELECT Count(1) FROM AD_User_App WHERE AD_User_ID=? and AD_OAuth2_Client_ID=? and IsActive='Y' ";
	private static final String DEFAULT_TOKEN_TYPE_BEARER = "Bearer";
	/**	Cache						*/
	private static CCache<String, MOAuth2ClientToken> clientTokenCache = new CCache<>("clientTokenCache", 500, 1);
	private static CCache<Integer,MClient>	clientCache = new CCache<Integer,MClient>(MClient.TABLE_NAME, 3, 120, true);
	
	
	public static final String LOCAL_TRX_PREFIX = "OAuth2";

	private static final String MSG_NOT_SUPPORTED = "NOT_SUPPORTED";
	
    public static final String PARAM_USERID_OAUTH_TOKEN = "ad_user_id";
    public static final String PARAM_CLIENTID_OAUTH_TOKEN = "ad_client_id";
    public static final String PARAM_ORGID_OAUTH_TOKEN = "ad_org_id";
    public static final String PARAM_TREEID_OAUTH_TOKEN = "ad_tree_id";
    public static final String PARAM_USERUU_OAUTH_TOKEN = "ad_user_uu";

    

	
    public static final String CLIENT_ID_PREFIX = "cadre.api.oauth2-client.";
   	

	/*public static void main(String [] args) throws OAuthSystemException {
		MD5Generator ge = new MD5Generator();
		System.out.println(ge.generateValue());
	}*/
	
	private ModelService genericService;

	public OAuth2ServiceImpl() {
		genericService = CDI.current().select(ModelService.class).get();
	}
	
	/**
	 * Find valid token
	 * @param token
	 * @return
	 * @throws OAuthException
	 */
	@Override
	public void parseToken(String hashToken) throws OAuthException {
		if (StringUtils.isNotEmpty(hashToken)) {
			try {
				MOAuth2ClientToken clientToken = clientTokenCache.get(hashToken);
				boolean found = true;
				if (clientToken==null) {
					
					
					int currentClientID = RDBMS.getSQLValueEx(
							null, 
							SQL_GET_AD_CLIENT_FROM_ACCESS_TOKEN,
							hashToken);
					
					CadreEnv.setContextValue(CadreEnv.AD_CLIENT_ID, currentClientID);
					
					
					clientToken= genericService.getPO(CadreEnv.getTrxName(), MOAuth2ClientToken.TABLE_NAME,MOAuth2ClientToken.COLUMNNAME_AccessToken , hashToken);
					found=false;
				}
				
				
				if (clientToken.isAccessTokenValid()) {

					CadreEnv.setContextValue(CadreEnv.AD_CLIENT_ID, clientToken.getAD_Client_ID());
					CadreEnv.setContextValue(CadreEnv.AD_ORG_ID, clientToken.getAD_Org_ID());
					CadreEnv.setContextValue(CadreEnv.AD_OAuth2_Client_ID, clientToken.getAD_OAuth2_Client_ID());
					CadreEnv.setContextValue(CadreEnv.AD_App_ID, clientToken.getAD_App_ID());

					
					MUser user =genericService.getPO(CadreEnv.getTrxName(), MUser.TABLE_NAME,clientToken.getAD_User_ID());
					SecurityUtils.validateUser(CadreEnv.getTrxName(),user);


					if (!found) {
						clientTokenCache.put(hashToken, clientToken);					
					}
					
				}else {
					throw new OAuthException(OAuthError.ResourceResponse.EXPIRED_TOKEN);
				}
				
			}catch (CadreException ex) {
				LOGGER.error("parseToken("+hashToken+")= INVALID_TOKEN", ex);

				throw new OAuthException(OAuthError.ResourceResponse.INVALID_TOKEN);
			}
			
		}else {
			LOGGER.error("parseToken("+hashToken+")= INVALID_REQUEST");
			throw new OAuthException(OAuthError.ResourceResponse.INVALID_REQUEST);
		}

	}


	@Override
	public OAuthResponse processOAuthTokenRequest(OAuthTokenRequest oauthRequest) throws OAuthSystemException {
		Trx trx = Trx.get(Trx.createTrxName(LOCAL_TRX_PREFIX), true);

		
        OAuthResponse [] oAuthResponse = new OAuthResponse[1];
        
			try {
				switch (oauthRequest.getGrantType()) {
				case SecurityUtils.GRANT_TYPE_PASSWORD:
					oAuthResponse[0]= logInResourceOwnerCredentials(trx.getTrxName(),oauthRequest);
					break;
				case SecurityUtils.GRANT_TYPE_CLIENT_CREDENTIALS:
					oAuthResponse[0]= logInClientCredentials(trx.getTrxName(),oauthRequest);
					break;
				case SecurityUtils.GRANT_TYPE_AUTHORIZATION_CODE:
					oAuthResponse[0]= logInAuthorizationCode(trx.getTrxName(),oauthRequest);
					break;
				case SecurityUtils.GRANT_TYPE_REFRESH_TOKEN:
					oAuthResponse[0]= refreshToken(trx.getTrxName(),oauthRequest);
					break;
				default:
					oAuthResponse[0] = OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
							.setError(OAuthError.TokenResponse.INVALID_GRANT).buildJSONMessage();
				}

			} catch (OAuthSystemException | CadreException e) {		
				if (trx != null) {
					trx.rollback();
					trx.close();
					trx = null;
				}
				
				if (e.getCause() instanceof CadreException) {
					LOGGER.error("processOAuthTokenRequest()", e);
					throw (CadreException)e.getCause();
				}else {
					LOGGER.error("processOAuthTokenRequest() = UNAUTHORIZED", e);

					throw new CadreException(Status.UNAUTHORIZED.getStatusCode(),e.getMessage());
				}
			}finally {
				if (trx != null) {
					trx.commit();
					trx.close();
				}
			}
		
        return oAuthResponse[0];

	}
	

	private OAuthResponse logInResourceOwnerCredentials(String trxName, OAuthTokenRequest oauthRequest) throws OAuthSystemException{
        
		try {
			MOAuth2Client oauthClient = SecurityUtils.validateClient(trxName, oauthRequest.getClientId(),
					oauthRequest.getClientSecret());
			
			int currentClientID = RDBMS.getSQLValueEx(trxName, SQL_GET_AD_CLIENT_FROM_AD_USER,  oauthRequest.getUsername().trim());
			
			CadreEnv.setContextValue(CadreEnv.AD_CLIENT_ID, currentClientID);
			
			SearchQuery queryUser = new JDBCQueryImpl.Builder(MUser.TABLE_NAME)
					.and(GenericCondition.equals(MUser.COLUMNNAME_EMailUser, oauthRequest.getUsername().trim()))
					.and(GenericCondition.equals(MUser.COLUMNNAME_UserPIN,PasswordUtils.getHash(oauthRequest.getPassword())))
					.build();

			SearchResult<MUser> userResult = genericService.search(trxName, queryUser);
			MUser user = userResult.getSingleResult(false);
			
			if (user==null) {
				throw new OAuthException(OAuthError.CodeResponse.ACCESS_DENIED);
			}
			
			SecurityUtils.validateUser(trxName,user);
			
			user.setDateLastLogin(new Timestamp(System.currentTimeMillis()));
			genericService.save(user);
				
			final MOAuth2ClientToken token = generateOAuthToken(trxName, oauthClient, user);
			
			return generateTokenResponse(user,oauthClient,token);



		} catch (CadreException e) {
			LOGGER.error("logInResourceOwnerCredentials(clientId="+oauthRequest.getClientId()+")", e);

			throw new OAuthSystemException(e);
		}

	}

	@Override
	public MOAuth2ClientToken generateOAuthToken(String trxName, MOAuth2Client oauthClient,MUser user) throws OAuthSystemException {
     
		OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());

		try {
			//Create Token
			CadreEnv.setContextValue(CadreEnv.AD_CLIENT_ID, user.getAD_Client_ID());
			CadreEnv.setContextValue(CadreEnv.AD_ORG_ID, user.getAD_Org_ID());

			RDBMS.executeUpdate(trxName, SQL_INVALIDATE_OLD_TOKENS, new Object[] {user.getAD_User_ID(), oauthClient.getAD_OAuth2_Client_ID()}, 0);
			
        	MOAuth2ClientToken clientToken = genericService.createPO(trxName,MOAuth2ClientToken.TABLE_NAME);
        	
        	clientToken.setAD_User_ID(user.getAD_User_ID());
        	clientToken.setAccessToken(oauthIssuerImpl.accessToken());
        	clientToken.setAccessTokenExpiration(oauthClient.calculateAccessTokenExpiration());        		
        	clientToken.setIsActiveAccessToken(true);
        	clientToken.setAD_OAuth2_Client_ID(oauthClient.getAD_OAuth2_Client_ID());
        	clientToken.setAD_App_ID(oauthClient.getAD_App_ID());

        	if (StringUtils.isNoneEmpty(oauthIssuerImpl.refreshToken())) {
	            clientToken.setRefreshToken(oauthIssuerImpl.refreshToken());
	            clientToken.setActiveRefreshToken(true);
	            clientToken.setRefreshTokenExpiration(oauthClient.calculateRefreshTokenExpiration());
        	}

        	/*if (StringUtils.isNoneEmpty(authorizationCode)) {
        		clientToken.setAuthorizationCode(authorizationCode);
            	clientToken.setAuthorizationCodeExpiration(oauthClient.calculateAuthorizationCodeExpiration());
            	clientToken.setIsActiveAuthorizationCode(true);
        	}*/
        	
        	genericService.save(clientToken);
        	
        	return clientToken;
        	
        }catch(CadreException e) {
			LOGGER.error("generateOAuthToken(user="+user.getAD_User_ID()+")", e);
        	throw new OAuthSystemException(e);
        }

	}

	private OAuthResponse refreshToken(String trxName, OAuthTokenRequest oauthRequest) throws OAuthSystemException {
		
		final String refreshToken = oauthRequest.getRefreshToken();
		
		if (StringUtils.isNotEmpty(refreshToken)) {
			
			int currentClientID = RDBMS.getSQLValueEx(
					trxName, 
					SQL_GET_AD_CLIENT_FROM_REFRESH_TOKEN,
					refreshToken);
			
			CadreEnv.setContextValue(CadreEnv.AD_CLIENT_ID, currentClientID);
			
			MOAuth2ClientToken  clientToken=
					genericService.getPO(trxName, MOAuth2ClientToken.TABLE_NAME,MOAuth2ClientToken.COLUMNNAME_RefreshToken , refreshToken);
			
			MOAuth2Client oauthClient = SecurityUtils.validateClient(trxName,oauthRequest.getClientId(),oauthRequest.getClientSecret());

			MUser user =genericService.getPO(trxName, MUser.TABLE_NAME,clientToken.getAD_User_ID());
			
			if (!clientToken.isAccessTokenValid() && clientToken.isRefreshTokenValid()) {
				
				
				SecurityUtils.validateUser(trxName,user);
				
		        OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
		        		        
	        	clientToken.setAccessToken(oauthIssuerImpl.accessToken());
	        	clientToken.setAccessTokenExpiration(oauthClient.calculateAccessTokenExpiration());  
	        	
	        	if (StringUtils.isNoneEmpty(oauthIssuerImpl.refreshToken())) {
		            clientToken.setRefreshToken(oauthIssuerImpl.refreshToken());
		            clientToken.setActiveRefreshToken(true);
		            clientToken.setRefreshTokenExpiration(oauthClient.calculateRefreshTokenExpiration());
	        	}
	            
	            genericService.save(clientToken);
	            
				return generateTokenResponse(user,oauthClient,clientToken);
	            
				
			}else if (clientToken.isAccessTokenValid()) {
				return generateTokenResponse(user,oauthClient,clientToken);
			}else {
		        return OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
		                .setError(OAuthError.ResourceResponse.EXPIRED_TOKEN)
		                .setErrorDescription(MSG_NOT_SUPPORTED)
		                .buildJSONMessage();	
			}
			
		}else {
	        return OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
	                .setError(OAuthError.TokenResponse.UNSUPPORTED_GRANT_TYPE)
	                .setErrorDescription(MSG_NOT_SUPPORTED)
	                .buildJSONMessage();	
		}
		

	}

	private OAuthResponse logInAuthorizationCode(String trxName, OAuthTokenRequest oauthRequest) throws OAuthSystemException{
        return OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                .setError(OAuthError.TokenResponse.UNSUPPORTED_GRANT_TYPE)
                .setErrorDescription(MSG_NOT_SUPPORTED)
                .buildJSONMessage();		
	}


	private OAuthResponse logInClientCredentials(String trxName, OAuthTokenRequest oauthRequest) throws OAuthSystemException{
		
		try {

			MOAuth2Client oauthClient = SecurityUtils.validateClient(trxName, oauthRequest.getClientId(),
					oauthRequest.getClientSecret());
			MUser user = genericService.getPO(trxName, MUser.TABLE_NAME, oauthClient.getAD_User_ID());

			SecurityUtils.validateUser(trxName,user);
			
			user.setDateLastLogin(new Timestamp(System.currentTimeMillis()));
			genericService.save(user);

			final MOAuth2ClientToken token = generateOAuthToken(trxName, oauthClient, user);

			return generateTokenResponse(user,oauthClient,token);

		} catch (CadreException e) {
			LOGGER.error("logInClientCredentials(clientId="+oauthRequest.getClientId()+")", e);

			throw new OAuthSystemException(e);
		}

	}

    /**
     * Generates a Token Response with the values of the token.
     * 
     * @param token
     * @param adUserID
     * @param refreshToken
     * 
     * @return OAuthResponse
     * @throws OAuthSystemException
     */
    public OAuthResponse generateTokenResponse(MUser user, MOAuth2Client oauthClient, MOAuth2ClientToken token) throws OAuthSystemException {
        
		MClient client = clientCache.get(token.getAD_Client_ID());
		if (client==null) {
			client = genericService.getPO(null, MClient.TABLE_NAME, token.getAD_Client_ID());
			clientCache.put(token.getAD_Client_ID(), client);
		}
		
        OAuthTokenResponseBuilder tokenResponseBuilder = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
                .setAccessToken(token.getAccessToken())
                .setRefreshToken(token.getRefreshToken())
                .setParam(PARAM_USERID_OAUTH_TOKEN, String.valueOf(token.getAD_User_ID()))
                .setParam(PARAM_ORGID_OAUTH_TOKEN, String.valueOf(token.getAD_Org_ID()))
                .setParam(PARAM_CLIENTID_OAUTH_TOKEN, String.valueOf(token.getAD_Client_ID()))
                .setParam(PARAM_TREEID_OAUTH_TOKEN, String.valueOf(client.getAD_Tree_ID()))
                .setParam(PARAM_USERUU_OAUTH_TOKEN, user.getAD_User_UU())

                .setExpiresIn(Long.toString(oauthClient.getTokenExpiresIn()))
                .setTokenType(DEFAULT_TOKEN_TYPE_BEARER);


        if (StringUtils.isNotEmpty(token.getRefreshToken())) {
            tokenResponseBuilder.setRefreshToken(token.getRefreshToken());
        }

        return tokenResponseBuilder.buildJSONMessage();
    }

}


/**

OAuthError.CodeResponse
String	ACCESS_DENIED	The resource owner or authorization server denied the request.
String	INVALID_REQUEST	The request is missing a required parameter, includes an unsupported parameter value, or is otherwise malformed.
String	INVALID_SCOPE	The requested scope is invalid, unknown, or malformed.
String	SERVER_ERROR	The authorization server encountered an unexpected condition which prevented it from fulfilling the request.
String	TEMPORARILY_UNAVAILABLE	The authorization server is currently unable to handle the request due to a temporary overloading or maintenance of the server.
String	UNAUTHORIZED_CLIENT	The client is not authorized to request an authorization code using this method.
String	UNSUPPORTED_RESPONSE_TYPE	The authorization server does not support obtaining an authorization code using this method.


OAuthError.ResourceResponse
String	EXPIRED_TOKEN	
String	INSUFFICIENT_SCOPE	The request requires higher privileges than provided by the access token.
String	INVALID_REQUEST	The request is missing a required parameter, includes an unsupported parameter value, repeats a parameter, includes multiple credentials, utilizes more than one mechanism for authenticating the client, or is otherwise malformed.
String	INVALID_TOKEN	The access token provided is expired, revoked, malformed, or invalid for other reasons.

OAuthError.TokenResponse
String	INVALID_CLIENT	Client authentication failed (e.g.
String	INVALID_GRANT	The provided authorization grant (e.g.
String	INVALID_REQUEST	The request is missing a required parameter, includes an unsupported parameter value, repeats a parameter, includes multiple credentials, utilizes more than one mechanism for authenticating the client, or is otherwise malformed.
String	INVALID_SCOPE	The requested scope is invalid, unknown, malformed, or exceeds the scope granted by the resource owner.
String	UNAUTHORIZED_CLIENT	The authenticated client is not authorized to use this authorization grant type.
String	UNSUPPORTED_GRANT_TYPE	



*/