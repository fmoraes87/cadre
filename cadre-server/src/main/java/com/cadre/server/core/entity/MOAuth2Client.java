package com.cadre.server.core.entity;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MOAuth2Client.TABLE_NAME)
public class MOAuth2Client extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "AD_OAuth2_Client";
	public static final int TABLE_ID = 2;

	/** Column name AD_App_ID */
	public static final String COLUMNNAME_AD_App_ID = "AD_App_ID";
	/** Column name AD_OAuth2_Client_ID */
	public static final String COLUMNNAME_AD_OAuth2_Client_ID = "AD_OAuth2_Client_ID";
	/** Column name Description */
	public static final String COLUMNNAME_Description = "Description";
	/** Column name Name */
	public static final String COLUMNNAME_Name = "Name";
	/** Column name ClientID */
	public static final String COLUMNNAME_ClientID = "ClientID";
	/** Column name ClientSecret */
	public static final String COLUMNNAME_ClientSecret = "ClientSecret";
	/** Column name IsLocked */
	public static final String COLUMNNAME_IsLocked = "IsLocked";
	/** Column name DateAccountLocked */
	public static final String COLUMNNAME_DateAccountLocked = "DateAccountLocked";
	/** Column name IsAdmin */
	public static final String COLUMNNAME_IsAdmin = "IsAdmin";
	/** Column name TokenExpiresIn */
	public static final String COLUMNNAME_TokenExpiresIn = "TokenExpiresIn";
	/** Column name AD_User_ID */
	public static final String COLUMNNAME_AD_User_ID = "AD_User_ID";
	/** Column name IsRefreshTokenExpires */
	public static final String COLUMNNAME_IsRefreshTokenExpires = "IsRefreshTokenExpires";
	/** Column name RefreshTokenValidity */
	public static final String COLUMNNAME_RefreshTokenValidity = "RefreshTokenValidity";

	/**
	 * Get User/Contact.
	 * 
	 * @return User within the system - Internal or Business Partner Contact
	 */
	public int getAD_User_ID() {
		return (Integer) getValueNoCheck(COLUMNNAME_AD_User_ID);
	}

	/**
	 * Get OAuth2 Client.
	 * 
	 * @return OAuth2 Client
	 */
	public int getAD_OAuth2_Client_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_OAuth2_Client_ID);
		if (ii == null) {
			return 0;
		}
		return ii.intValue();
	}

	/**
	 * Is Locked
	 * 
	 * @return is Locked
	 */
	public final boolean isLocked() {
		Boolean bb = (Boolean) getValueNoCheck(COLUMNNAME_IsLocked);
		if (bb != null)
			return bb.booleanValue();
		return false;
	} // isLocked

	/**
	 * Set Client ID.
	 * 
	 * @param ClientID Client ID
	 */
	public void setClientID(String clientID) {
		setValueNoCheck(COLUMNNAME_ClientID, clientID);
	}

	/**
	 * Get Client ID.
	 * 
	 * @return Client ID
	 */
	public String getClientID() {
		return (String) getValueNoCheck(COLUMNNAME_ClientID);
	}

	/**
	 * Set Client Secret.
	 * 
	 * @param clientSecret Client Secret
	 */
	public void setClientSecret(String clientSecret) {
		setValueNoCheck(COLUMNNAME_ClientSecret, clientSecret);
	}

	/**
	 * Get Client Secret.
	 * 
	 * @return Client Secret
	 */
	public String getClientSecret() {
		return (String) getValueNoCheck(COLUMNNAME_ClientSecret);
	}

	/**
	 * Get TokenExpiresIn.
	 * 
	 * @return TokenExpiresIn
	 */
	public Integer getTokenExpiresIn() {
		return (Integer) getValueNoCheck(COLUMNNAME_TokenExpiresIn);
	}

	/**
	 * Set Refresh Token Validity.
	 * 
	 * @param RefreshTokenValidity Refresh Token Validity
	 */
	public void setRefreshTokenValidity(int refreshTokenValidity) {
		setValueNoCheck(COLUMNNAME_RefreshTokenValidity, Integer.valueOf(refreshTokenValidity));
	}

	/**
	 * Set Refresh Token Expires.
	 * 
	 * @param FH_IsRefreshTokenExpires Refresh Token Expires
	 */
	public void setRefreshTokenExpires(boolean isRefreshTokenExpires) {
		setValueNoCheck(COLUMNNAME_IsRefreshTokenExpires, Boolean.valueOf(isRefreshTokenExpires));
	}

	/**
	 * Get Refresh Token Expires.
	 * 
	 * @return Refresh Token Expires
	 */
	public boolean isRefreshTokenExpires() {
		Object oo = getValueNoCheck(COLUMNNAME_IsRefreshTokenExpires);
		if (oo != null) {
			if (oo instanceof Boolean)
				return ((Boolean) oo).booleanValue();
			return "Y".equals(oo);
		}
		return false;
	}

    /**
     * Calculate the access token expiration date based on the OAuth Client Configuration.
     * 
     * @return {@link Timestamp} Expiration Date of the Refresh Token
     */
    public Timestamp calculateAccessTokenExpiration() {
    	if ( getTokenExpiresIn() > 0) {
    		Date calculatedDate = DateUtils.addSeconds(new Date(System.currentTimeMillis()), getTokenExpiresIn());
    		return new Timestamp(calculatedDate.getTime());    		
    	}
    	
    	return null;
    }

    
	/**
	 * Calculate the refresh token expiration date based on the OAuth Client
	 * Configuration. Null is returned if it doesn't expire.
	 * 
	 * @return {@link Timestamp} Expiration Date of the Access Token
	 */
	public Timestamp calculateRefreshTokenExpiration() {
		if (isRefreshTokenExpires()) {
			Date calculatedDate = DateUtils.addSeconds(new Date(System.currentTimeMillis()),getRefreshTokenValidity());
			return new Timestamp(calculatedDate.getTime());
		}
		return null;
	}

	/**
	 * Get Refresh Token Validity.
	 * 
	 * @return Refresh Token Validity
	 */
	public int getRefreshTokenValidity() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_RefreshTokenValidity);
		if (ii == null)
			return 0;
		return ii.intValue();
	}
	
	/**
	 * 
	 * @param AD_App_ID
	 */
	public void setAD_App_ID(int AD_App_ID) {
		if (AD_App_ID < 0)
			setValueNoCheck(COLUMNNAME_AD_App_ID, null);
		else
			setValueNoCheck(COLUMNNAME_AD_App_ID, Integer.valueOf(AD_App_ID));
	}

	/**
	 * 
	 * @return
	 */
	public int getAD_App_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_App_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}	
	
	
	/**
	 *	Is Admin
	 *  @return is admin
	 */
	public final boolean isAdmin()
	{
		Boolean bb = (Boolean)getValueOfColumn(COLUMNNAME_IsAdmin);
		if (bb != null)
			return bb.booleanValue();
		return false;
	}	//	isActive
	

	public boolean isValid() {
		return isActive() && !isLocked();
	}

}
