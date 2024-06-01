package com.cadre.server.core.entity;

import java.sql.Timestamp;
import java.util.Date;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MOAuth2ClientToken.TABLE_NAME)
public class MOAuth2ClientToken extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int TABLE_ID = 3;

	public static final String TABLE_NAME = "AD_OAuth2_Client_Token";
	/** Column name AD_User_ID */
	public static final String COLUMNNAME_AD_User_ID = "AD_User_ID";
	/** Column name AD_OAuth2ClientToken_UU */
	public static final String COLUMNNAME_AD_OAuth2_Client_Token_UU = "AD_OAuth2_Client_Token_UU";
	/** Column name AD_OAuth2ClientToken_ID */
	public static final String COLUMNNAME_AD_OAuth2_Client_Token_ID = "AD_OAuth2_Client_Token_ID";
	/** Column name AD_OAuth2ClientToken_ID */
	public static final String COLUMNNAME_AD_OAuth2_Client_ID = "AD_OAuth2_Client_ID";
	/** Column name AccessToken */
	public static final String COLUMNNAME_AccessToken = "AccessToken";
	/** Column name AccessTokenExpiration */
	public static final String COLUMNNAME_AccessTokenExpiration = "AccessTokenExpiration";
	/** Column name AuthorizationCode */
	public static final String COLUMNNAME_AuthorizationCode = "AuthorizationCode";
	/** Column name AuthorizationCodeExpiration */
	public static final String COLUMNNAME_AuthorizationCodeExpiration = "AuthorizationCodeExpiration";
	/** Column name IsActiveAccessToken */
	public static final String COLUMNNAME_IsActiveAccessToken = "IsActiveAccessToken";
	/** Column name RefreshToken */
	public static final String COLUMNNAME_RefreshToken = "RefreshToken";
	/** Column name RefreshTokenExpiration */
	public static final String COLUMNNAME_RefreshTokenExpiration = "RefreshTokenExpiration";
	/** Column name IsActiveRefreshToken */
	public static final String COLUMNNAME_IsActiveRefreshToken = "IsActiveRefreshToken";
	/** Column name IsActiveRefreshToken */
	public static final String COLUMNNAME_AD_App_ID = "AD_App_ID";
	/**
	 * Set User/Contat
	 * 
	 * @param AD_User_ID org
	 */
	final public void setAD_User_ID(int AD_User_ID) {
		setValueNoCheck(COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
	} // setAD_User_ID

	/**
	 * Get User/Contact.
	 * 
	 * @return User within the system - Internal or Business Partner Contact
	 */
	public int getAD_User_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_User_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Get Access Token Expiration Date.
	 * 
	 * @return Access Token Expiration Date
	 */
	public Timestamp getAccessTokenExpiration() {
		return (Timestamp) getValueNoCheck(COLUMNNAME_AccessTokenExpiration);
	}

	/**
	 * Set Access Token Expiration Date.
	 * 
	 * @param AccessTokenExpiration Access Token Expiration Date
	 */
	public void setAccessTokenExpiration(Timestamp accessTokenExpiration) {
		setValueNoCheck(COLUMNNAME_AccessTokenExpiration, accessTokenExpiration);
	}

	/**
	 * Set Access Token.
	 * 
	 * @param accessToken Access Token
	 */
	public void setAccessToken(String accessToken) {
		setValueNoCheck(COLUMNNAME_AccessToken, accessToken);
	}

	/**
	 * Get Access Token.
	 * 
	 * @return Access Token
	 */
	public String getAccessToken() {
		return (String) getValueNoCheck(COLUMNNAME_AccessToken);
	}

	/**
	 * Set Refresh Token.
	 * 
	 * @param refreshToken Refresh Token
	 */
	public void setRefreshToken(String refreshToken) {
		setValueNoCheck(COLUMNNAME_RefreshToken, refreshToken);
	}

	/**
	 * Get Access Token.
	 * 
	 * @return Access Token
	 */
	public String getRefreshToken() {
		return (String) getValueNoCheck(COLUMNNAME_RefreshToken);
	}

	/**
	 * Set Access Token Active.
	 * 
	 * @param IsActiveAccessToken Access Token Active
	 */
	public void setIsActiveAccessToken(boolean isActiveAccessToken) {
		setValueNoCheck(COLUMNNAME_IsActiveAccessToken, Boolean.valueOf(isActiveAccessToken));
	}

	/**
	 * Get Access Token Active.
	 * 
	 * @return Access Token Active
	 */
	public boolean isActiveAccessToken() {
		Object oo = getValueNoCheck(COLUMNNAME_IsActiveAccessToken);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return POModel.YES_VALUE.equals(oo);
		}
		return false;
	}

	/**
	 * Set Refresh Token Active.
	 * 
	 * @param IsActiveRefreshToken Refresh Token Active
	 */
	public void setActiveRefreshToken(boolean isActiveRefreshToken) {
		setValueNoCheck(COLUMNNAME_IsActiveRefreshToken, Boolean.valueOf(isActiveRefreshToken));
	}

	/**
	 * Get Refresh Token Active.
	 * 
	 * @return Refresh Token Active
	 */
	public boolean isActiveRefreshToken() {
		Object oo = getValueNoCheck(COLUMNNAME_IsActiveRefreshToken);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

	/**
	 * Set OAuth2 Client.
	 * 
	 * @param AD_OAuth2Client_ID OAuth2 Client
	 */
	public void setAD_OAuth2_Client_ID(int AD_OAuth2_Client_ID) {
		if (AD_OAuth2_Client_ID < 0)
			setValueNoCheck(COLUMNNAME_AD_OAuth2_Client_ID, null);
		else
			setValueNoCheck(COLUMNNAME_AD_OAuth2_Client_ID, Integer.valueOf(AD_OAuth2_Client_ID));
	}

	/**
	 * Get OAuth2 Client.
	 * 
	 * @return OAuth2 Client
	 */
	public int getAD_OAuth2_Client_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_OAuth2_Client_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set Refresh Token Expiration Date.
	 * 
	 * @param refreshTokenExpiration Refresh Token Expiration Date
	 */
	public void setRefreshTokenExpiration(Timestamp refreshTokenExpiration) {
		setValueNoCheck(COLUMNNAME_RefreshTokenExpiration, refreshTokenExpiration);
	}

	/**
	 * Get Refresh Token Expiration Date.
	 * 
	 * @return Refresh Token Expiration Date
	 */
	public Timestamp getRefreshTokenExpiration() {
		return (Timestamp) getValueNoCheck(COLUMNNAME_RefreshTokenExpiration);
	}

	/**
	 * Validate if the token is expired
	 * 
	 * @return {@link Boolean} true if this token is expired, false otherwise
	 */
	public boolean isAccessTokenValid() {
		return isActive() && isActiveAccessToken() && !isAccessTokenExpired();
	}

	/**
	 * Set OAuth2 Client.
	 * 
	 * @param AD_OAuth2Client_ID OAuth2 Client
	 */
	public void setAD_App_ID(int AD_App_ID) {
		if (AD_App_ID < 0)
			setValueNoCheck(COLUMNNAME_AD_App_ID, null);
		else
			setValueNoCheck(COLUMNNAME_AD_App_ID, Integer.valueOf(AD_App_ID));
	}

	/**
	 * Get OAuth2 Client.
	 * 
	 * @return OAuth2 Client
	 */
	public int getAD_App_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_App_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	
	/**
	 * Validate if the token is expired
	 * 
	 * @return {@link Boolean} true if this token is expired, false otherwise
	 */
	public boolean isRefreshTokenValid() {
		return isActive() && isActiveRefreshToken() && !isRefreshTokenExpired();
	}

	public boolean isAccessTokenExpired() {
		return getAccessTokenExpiration() != null && new Date().after(getAccessTokenExpiration());
	}

	public boolean isRefreshTokenExpired() {
		return getRefreshTokenExpiration() != null && new Date().after(getRefreshTokenExpiration());
	}

}
