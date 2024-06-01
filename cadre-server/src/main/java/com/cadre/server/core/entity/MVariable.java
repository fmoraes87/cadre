package com.cadre.server.core.entity;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MVariable.TABLE_NAME)
public class MVariable extends POModel  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** TableName=AD_Variable */
	public static final String TABLE_NAME = "AD_Variable";

	/** Column name AD_Variable_ID */
	public static final String COLUMNNAME_AD_Variable_ID = "AD_Variable_ID";

	/** Column name AD_Reference_ID */
	public static final String COLUMNNAME_AD_Reference_ID = "AD_Reference_ID";

	/** Column name Classname */
	public static final String COLUMNNAME_Classname = "Classname";

	/** Column name ColumnSQL */
	public static final String COLUMNNAME_ColumnSQL = "ColumnSQL";

	/** Column name ConstantValue */
	public static final String COLUMNNAME_ConstantValue = "ConstantValue";

	/** Column name Description */
	public static final String COLUMNNAME_Description = "Description";

	/** Column name Type */
	// Type of Validation (SQL, Java Script, Java Language)
	public static final String COLUMNNAME_Type = "Type";

	/** Column name Value */
	public static final String COLUMNNAME_Value = "Value";

	/**
	 * Set Reference.
	 * 
	 * @param AD_Reference_ID System Reference and Validation
	 */
	public void setAD_Reference_ID(int AD_Reference_ID) {
		if (AD_Reference_ID < 1)
			setValueNoCheck(COLUMNNAME_AD_Reference_ID, null);
		else
			setValueNoCheck(COLUMNNAME_AD_Reference_ID, Integer.valueOf(AD_Reference_ID));
	}

	/**
	 * Get Reference.
	 * 
	 * @return System Reference and Validation
	 */
	public int getAD_Reference_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_Reference_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set Classname.
	 * 
	 * @param Classname Java Classname
	 */
	public void setClassname(String Classname) {
		setValueNoCheck(COLUMNNAME_Classname, Classname);
	}

	/**
	 * Get Classname.
	 * 
	 * @return Java Classname
	 */
	public String getClassname() {
		return (String) getValueNoCheck(COLUMNNAME_Classname);
	}

	/**
	 * Set Column SQL.
	 * 
	 * @param ColumnSQL Virtual Column (r/o)
	 */
	public void setColumnSQL(String ColumnSQL) {
		setValueNoCheck(COLUMNNAME_ColumnSQL, ColumnSQL);
	}

	/**
	 * Get Column SQL.
	 * 
	 * @return Virtual Column (r/o)
	 */
	public String getColumnSQL() {
		return (String) getValueNoCheck(COLUMNNAME_ColumnSQL);
	}

	/**
	 * Set Constant Value.
	 * 
	 * @param ConstantValue Constant value
	 */
	public void setConstantValue(String ConstantValue) {
		setValueNoCheck(COLUMNNAME_ConstantValue, ConstantValue);
	}

	/**
	 * Get Constant Value.
	 * 
	 * @return Constant value
	 */
	public String getConstantValue() {
		return (String) getValueNoCheck(COLUMNNAME_ConstantValue);
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
	 * Set Variable.
	 * 
	 * @param Variable_ID Variable
	 */
	public void setAD_Variable_ID(int AD_Variable_ID) {
		if (AD_Variable_ID < 1)
			setValueNoCheck(COLUMNNAME_AD_Variable_ID, null);
		else
			setValueNoCheck(COLUMNNAME_AD_Variable_ID, Integer.valueOf(AD_Variable_ID));
	}

	/**
	 * Get Variable.
	 * 
	 * @return Variable
	 */
	public int getAD_Variable_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_Variable_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}
	
	/** SQL = 1 */
	public static final String TYPE_SQL = "1";
	/** Classname = 2 */
	public static final String TYPE_Classname = "2";
	/** Static Value = 3 */
	public static final String TYPE_StaticValue = "3";
	/** Set Type.
		@param Type 
		Type of Validation (SQL, Java Script, Java Language)
	  */
	public void setType (String Type)
	{

		setValueNoCheck (COLUMNNAME_Type, Type);
	}

	/** Get Type.
		@return Type of Validation (SQL, Java Script, Java Language)
	  */
	public String getType () 
	{
		return (String)getValueNoCheck(COLUMNNAME_Type);
	}

	public boolean isSQLValue() {
        return getType().equals(TYPE_SQL);
    }

	public boolean isStaticValue() {
        return getType().equals(TYPE_StaticValue);
    }


	/** Set Search Key.
		@param Value 
		Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value)
	{
		setValueNoCheck (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue () 
	{
		return (String)getValueNoCheck(COLUMNNAME_Value);
	}
	

}
