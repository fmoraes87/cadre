package com.cadre.server.core.service;

import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;

import com.cadre.server.core.boundary.ServiceProvider;
import com.cadre.server.core.entity.MOAuth2Client;
import com.cadre.server.core.entity.MOAuth2ClientToken;
import com.cadre.server.core.entity.MUser;
import com.cadre.server.core.security.oauth2.OAuthException;

public interface OAuthService extends ServiceProvider {

	/**
	 * 
	 * @param token
	 * @return
	 * @throws OAuthException 
	 */
	public void parseToken(String token) throws OAuthSystemException;

	/**
	 * Processo OAuthTokenRequest
	 * @param oauthRequest
	 * @return oAuthResponse
	 * @throws OAuthSystemException 
	 */
	public OAuthResponse processOAuthTokenRequest(OAuthTokenRequest oauthRequest) throws OAuthSystemException;
	
	/**
	 * Generate Token
	 * @param trxName
	 * @param oauthClient
	 * @param user
	 * @param oauthIssuerImpl
	 * @return
	 * @throws OAuthSystemExceptio
	 */
	public MOAuth2ClientToken generateOAuthToken(String trxName, MOAuth2Client oauthClient,MUser user) throws OAuthSystemException;

	/**
	 * Generate token Response
	 * @param oauthClient
	 * @param token
	 * @return
	 * @throws OAuthSystemException
	 */
	public OAuthResponse generateTokenResponse(MUser user , MOAuth2Client oauthClient, MOAuth2ClientToken token) throws OAuthSystemException;

}
