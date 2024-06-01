package com.cadre.server.core.entity;

public class MSysConfig extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** TableName=AD_SysConfig */
	public static final String Table_Name = "AD_SysConfig";

	/** AD_Table_ID=50009 */
	public static final int Table_ID = 45;

	/** Column name AD_SysConfig_ID */
	public static final String COLUMNNAME_AD_SysConfig_ID = "AD_SysConfig_ID";

	/** Column name AD_SysConfig_UU */
	public static final String COLUMNNAME_AD_SysConfig_UU = "AD_SysConfig_UU";

	/** Column name Description */
	public static final String COLUMNNAME_Description = "Description";

	/** Column name Name */
	public static final String COLUMNNAME_Name = "Name";

	/** Column name Value */
	public static final String COLUMNNAME_Value = "Value";

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
}
