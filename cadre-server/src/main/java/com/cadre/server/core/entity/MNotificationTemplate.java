package com.cadre.server.core.entity;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MNotificationTemplate.TABLE_NAME)
public class MNotificationTemplate extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "AD_NotificationTemplate";
	public static final int TABLE_ID = 11;

	/** Column name Header */
	public static final String COLUMNNAME_Header = "Header";
	/** Column name Text */
	public static final String COLUMNNAME_Template = "Template";
	/** Column name Name */
	public static final String COLUMNNAME_Name = "Name";
	/** Column name Description */
	public static final String COLUMNNAME_Description = "Description";
    /** Column name IsAdmin */
    public static final String COLUMNNAME_IsParseTemplate = "IsParseTemplate";

	/**
	 *	IsParseTemplate
	 *  @return Is Parse Template
	 */
	public final boolean isParseTemplate()
	{
		Boolean bb = (Boolean)getValueNoCheck(COLUMNNAME_IsParseTemplate);
		if (bb != null)
			return bb.booleanValue();
		return false;
	}	//	isLocked
	
	/**
	 * Set Subject.
	 * 
	 * @param Header  Header (Subject)
	 */
	public void setHeader(String Header) {
		setValueNoCheck(COLUMNNAME_Header, Header);
	}

	/**
	 * Get Subject.
	 * 
	 * @return  Header (Subject)
	 */
	public String getHeader() {
		return (String) getValueNoCheck(COLUMNNAME_Header);
	}

	/**
	 * Set  Text.
	 * 
	 * @param Text Text used for  message
	 */
	public void setTemplate(String Text) {
		setValueNoCheck(COLUMNNAME_Template, Text);
	}

	/**
	 * Get  Text.
	 * 
	 * @return Text used for  message
	 */
	public String getTemplate() {
		return (String) getValueNoCheck(COLUMNNAME_Template);
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
	 * Set Description.
	 * 
	 * @param Description
	 */
	public void setDescription(String description) {
		setValueNoCheck(COLUMNNAME_Description, description);
	}

	/**
	 * Get Description.
	 * 
	 * @return Description
	 */
	public String getDecription() {
		return (String) getValueNoCheck(COLUMNNAME_Description);
	}


}
