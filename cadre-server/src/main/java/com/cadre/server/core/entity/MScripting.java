package com.cadre.server.core.entity;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MScripting.TABLE_NAME)
public class MScripting extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** TableName=AD_Scripting */
	public static final String TABLE_NAME = "AD_Scripting";
	
	/** AD_Table_ID= */
	public static final int TABLE_ID = 53;

	/** Column name AD_Scripting_ID */
	public static final String COLUMNNAME_AD_Scripting_ID = "AD_Scripting_ID";

	/** Column name AD_Scripting_UU */
	public static final String COLUMNNAME_AD_Scripting_UU = "AD_Scripting_UU";
	
	/** Column name Value */
	public static final String COLUMNNAME_Value = "Value";
	
	/** Column name Name */
	public static final String COLUMNNAME_Name = "Name";
	
	/** Column name Content */
	public static final String COLUMNNAME_Content = "Content";

	/** Column name Description */
	public static final String COLUMNNAME_Description = "Description";

	/** Column name EngineType */
	public static final String COLUMNNAME_EngineType = "EngineType";
	
	/** Aspect Orient Program = A */
	public static final String ENGINETYPE_AspectOrientProgram = "A";
	/** JSR 223 Scripting APIs = S */
	public static final String ENGINETYPE_JSR223ScriptingAPIs = "S";
	/** JSR 94 Engine Type API = R */
	public static final String ENGINETYPE_JSR94RuleEngineAPI = "R";
	

	public String getValue() {
		return (String) getValueNoCheck(COLUMNNAME_Value);
	}
	
	
	public String getName() {
		return (String) getValueNoCheck(COLUMNNAME_Name);
	}
	
	public String getContent() {
		return (String) getValueNoCheck(COLUMNNAME_Content);
	}
	
	/** Get Rule Type.
	@return Rule Type	  */
	public String getEngineType () 
	{
		return (String)getValueNoCheck(COLUMNNAME_EngineType);
	}


	public boolean isGroovy() {
		return ENGINETYPE_JSR223ScriptingAPIs.equals(getEngineType());
	}
	


}
