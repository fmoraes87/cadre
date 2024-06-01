package com.cadre.server.core.entity;

import java.sql.Timestamp;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MUser.TABLE_NAME)
public class MUser extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "AD_User";
	public static final int TABLE_ID = 12;

	/** Column name AD_User_ID */
	public static final String COLUMNNAME_AD_User_ID = "AD_User_ID";
	/** Column name AD_User_UU */
	public static final String COLUMNNAME_AD_User_UU = "AD_User_UU";
	/** Column name EMailUser */
	public static final String COLUMNNAME_EMailUser = "EMailUser";
	/** Column name UserPIN */
	public static final String COLUMNNAME_UserPIN = "UserPIN";
	/** Column name IsLocked */
	public static final String COLUMNNAME_IsLocked = "IsLocked";
	/** Column name DateAccountLocked */
	public static final String COLUMNNAME_DateAccountLocked = "DateAccountLocked";
	/** Column name DateLastLogin */
	public static final String COLUMNNAME_DateLastLogin = "DateLastLogin";
	/** Column name IsAccountVerified */
	public static final String COLUMNNAME_IsAccountVerified = "IsAccountVerified";
	/** Column name UserPIN */
	public static final String COLUMNNAME_Name = "Name";
    /** Column name UserLevel */
    public static final String COLUMNNAME_UserLevel = "UserLevel";
	/** Column name IsAdmin */
	public static final String COLUMNNAME_IsAdmin = "IsAdmin";
	/** Column name IsAdmin */
	public static final String COLUMNNAME_IsViewOnlyActiveRecords = "IsViewOnlyActiveRecords";
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
	 * Set User PIN.
	 * 
	 * @param UserPIN User PIN
	 */
	public void setUserPIN(String userPIN) {
		setValueNoCheck(COLUMNNAME_UserPIN, userPIN);
	}

	/**
	 * Get User PIN.
	 * 
	 * @return User PIN
	 */
	public String getUserPIN() {
		return (String) getValueNoCheck(COLUMNNAME_UserPIN);
	}

	/**
	 * Get EMail User ID.
	 * 
	 * @return 
	 */
	public String getEMailUser() {
		return (String) getValueNoCheck(COLUMNNAME_EMailUser);
	}
	
	/**
	 * Set Email User
	 * 
	 * @param EmailUser
	 */
	public void setEMailUser(String emailUser) {
		setValueNoCheck(COLUMNNAME_EMailUser, emailUser);
	}
	
	/**
	 * Set User PIN.
	 * 
	 * @param UserPIN User PIN
	 */
	public void setIsAccountVerified(boolean verified) {
		setValueNoCheck(COLUMNNAME_IsAccountVerified, Boolean.valueOf(verified));
	}

	/**
	 * Set Date Last Login.
	 * 
	 * @param DateLastLogin Date Last Login
	 */
	public void setDateLastLogin(Timestamp DateLastLogin) {
		setValueNoCheck(COLUMNNAME_DateLastLogin, DateLastLogin);
	}

	/**
	 * Is Account Verified
	 * 
	 * @return IsAccountVerified
	 */
	public final boolean isAccountVerified() {
		Boolean bb = (Boolean) getValueOfColumn(COLUMNNAME_IsAccountVerified);
		if (bb != null)
			return bb.booleanValue();
		return false;
	} // isActive

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
	 * Is View Only Active Records
	 * 
	 * @return is Locked
	 */
	public final boolean isViewOnlyActiveRecords() {
		Boolean bb = (Boolean) getValueNoCheck(COLUMNNAME_IsViewOnlyActiveRecords);
		if (bb != null)
			return bb.booleanValue();
		return false;
	} // isLocked

	
	
	/**
	 * Set Name
	 * 
	 * @param Name
	 */
	public void setName(String name) {
		setValueNoCheck(COLUMNNAME_Name, name);
	}
	
	/**
	 * Get Name
	 * 
	 * @return User Name (ID)
	 */
	public String getName() {
		return (String) getValueNoCheck(COLUMNNAME_Name);
	}
	
	/**
	 * Get AD_User_UU
	 * 
	 * @return AD_User_UU
	 */
	public String getAD_User_UU() {
		return (String) getValueNoCheck(COLUMNNAME_AD_User_UU);
	}
	

	/** Get User Level.
		@return System Client Organization
	  */
	public String getUserLevel () 
	{
		return (String)getValueNoCheck(COLUMNNAME_UserLevel);
	}


	/**
	 * Is Valid
	 * @return
	 */
    public boolean isValid() {
    	return isActive() && !isLocked();
    }
    
}
