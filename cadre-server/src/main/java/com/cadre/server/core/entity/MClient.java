package com.cadre.server.core.entity;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MClient.TABLE_NAME)
public class MClient extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "AD_Client";
	public static final int TABLE_ID = 11;

	/** Column name AD_MailConfig_ID */
	public static final String COLUMNNAME_AD_MailConfig_ID = "AD_MailConfig_ID";
	/** Column name Description */
	public static final String COLUMNNAME_Description = "Description";
	/** Column name Name */
	public static final String COLUMNNAME_Name = "Name";
    /** Column name Value */
    public static final String COLUMNNAME_Value = "Value";
	/** Column name AD_Tree_ID */
	public static final String COLUMNNAME_AD_Tree_ID = "AD_Tree_ID";
	
	/**
	 * Set Language.
	 * 
	 * @param AD_Language Language for this entity
	 */
	public void setAD_Language(String AD_Language) {

		setValueNoCheck(COLUMNNAME_LANGUAGE, AD_Language);
	}

	/**
	 * Get Language.
	 * 
	 * @return Language for this entity
	 */
	public String getAD_Language() {
		return (String) getValueNoCheck(COLUMNNAME_LANGUAGE);
	}

	/**
	 * Set Description.
	 * 
	 * @param Description Optional short description of the record
	 */
	public void setDescription(String Description) {
		setValueNoCheck(COLUMNNAME_Description, Description);
	}

	/**
	 * Get Description.
	 * 
	 * @return Optional short description of the record
	 */
	public String getDescription() {
		return (String) getValueNoCheck(COLUMNNAME_Description);
	}

	/**
	 * Set Name.
	 * 
	 * @param Name Alphanumeric identifier of the entity
	 */
	public void setName(String Name) {
		setValueNoCheck(COLUMNNAME_Name, Name);
	}

	/**
	 * Get Name.
	 * 
	 * @return Alphanumeric identifier of the entity
	 */
	public String getName() {
		return (String) getValueNoCheck(COLUMNNAME_Name);
	}

	/**
	 * Set Search Key.
	 * 
	 * @param Value Search key for the record in the format required - must be
	 *              unique
	 */
	public void setValue(String Value) {
		setValueNoCheck(COLUMNNAME_Value, Value);
	}

	/**
	 * Get Search Key.
	 * 
	 * @return Search key for the record in the format required - must be unique
	 */
	public String getValue() {
		return (String) getValueNoCheck(COLUMNNAME_Value);
	}
	
	/**
	 * Mail Config
	 * 
	 * @return Mail Config to send and receive e-mails
	 */
	public int getAD_MailConfig_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_MailConfig_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Get Tree
	 * 
	 * @return Main Tree for Client
	 */
	public int getAD_Tree_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_Tree_ID);
		if (ii == null) {
			return -1;
		}
		return ii.intValue();
	}

}
