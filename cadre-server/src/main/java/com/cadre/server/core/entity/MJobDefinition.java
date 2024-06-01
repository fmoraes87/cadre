package com.cadre.server.core.entity;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MJobDefinition.TABLE_NAME)
public class MJobDefinition extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "AD_JobDefinition";
	public static final int TABLE_ID = 48;

	/** Column name AD_JobDefinition_ID */
	public static final String COLUMNNAME_AD_JobDefinition_ID = "AD_JobDefinition_ID";
    /** Column name Value */
    public static final String COLUMNNAME_Value = "Value";
	/** Column name Name */
	public static final String COLUMNNAME_Name = "Name";
	/** Column name Description */
	public static final String COLUMNNAME_Description = "Description";
	/** Column name Description */
	public static final String COLUMNNAME_Help = "Help";
	/** Column name AD_Process_ID */
	public static final String COLUMNNAME_AD_Process_ID = "AD_Process_ID";
	/** Column name ProcedureName */
	public static final String COLUMNNAME_ProcedureName = "ProcedureName";	
	/** Column name AD_Scripting_ID */
	public static final String COLUMNNAME_AD_Scripting_ID = "AD_Scripting_ID";

	/**
	 * Set Description.
	 * 
	 * @param Description Optional short description of the record
	 */
	public void setDescription(String Description) {
		setValueNoCheck(COLUMNNAME_Description, Description);
	}
	
	/**
	 * Get Message Text.
	 * 
	 * @return Textual Informational, Menu or Error Message
	 */
	public String getName () 
	{
		return (String)getValueNoCheck(COLUMNNAME_Name);
	}

	/**
	 * Get Description.
	 * 
	 * @return Optional short description of the record
	 */
	public String getDescription() {
		return (String) getValueNoCheck(COLUMNNAME_Description);
	}

	public String getProcedureName() {
		return (String) getValueNoCheck(COLUMNNAME_ProcedureName);

	}
	
	public int getAD_Scripting_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_Scripting_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}
}
