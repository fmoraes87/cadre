package com.cadre.server.core.entity;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MUserApp.TABLE_NAME)
public class MUserApp extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** TableName=AD_User_App */
	public static final String TABLE_NAME = "AD_User_App";

	/** AD_Table_ID=157 */
	public static final int Table_ID = 43;

	/** Column name AD_Role_ID */
	public static final String COLUMNNAME_AD_App_ID = "AD_App_ID";

	/** Column name AD_User_ID */
	public static final String COLUMNNAME_AD_User_ID = "AD_User_ID";

	/** Column name AD_User_Roles_UU */
	public static final String COLUMNNAME_AD_User_Roles_UU = "AD_user_App_ID";
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
	

	/** Set User/Contact.
		@param AD_User_ID 
		User within the system - Internal or Business Partner Contact
	  */
	public void setAD_User_ID (int AD_User_ID)
	{
		if (AD_User_ID < 1) 
			setValueNoCheck (COLUMNNAME_AD_User_ID, null);
		else 
			setValueNoCheck (COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
	}

	/** Get User/Contact.
		@return User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID () 
	{
		Integer ii = (Integer)getValueNoCheck(COLUMNNAME_AD_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

}
