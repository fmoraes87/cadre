package com.cadre.server.core.entity;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MProcess.TABLE_NAME)
public class MProcess extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** TableName=AD_Process */
	public static final String TABLE_NAME = "AD_Process";
	/** AD_Table_ID= */
	public static final int TABLE_ID = 10;
	/** Column name AD_Process_ID */
	public static final String COLUMNNAME_AD_Process_ID = "AD_Process_ID";
	/** Column name AD_Process_UU */
	public static final String COLUMNNAME_AD_Process_UU = "AD_Process_UU";
	/** Column name Description */
	public static final String COLUMNNAME_Description = "Description";
	/** Column name Value */
	public static final String COLUMNNAME_Value = "Value";
	/** Column name ProcedureName */
	public static final String COLUMNNAME_ProcedureName = "ProcedureName";
	/** Column name Help */
	public static final String COLUMNNAME_Help = "Help";
	/** Column name AD_Extension_ID */
	public static final String COLUMNNAME_AD_Extension_ID = "AD_Extension_ID";
	/** Column name AD_Scripting_ID */
	public static final String COLUMNNAME_AD_Scripting_ID = "AD_Scripting_ID";

	public MProcess() {

	}

	/**
	 * Set Process.
	 * 
	 * @param AD_Process_ID Process or Report
	 */
	public void setAD_Process_ID(int AD_Process_ID) {
		if (AD_Process_ID < 1)
			setValueNoCheck(COLUMNNAME_AD_Process_ID, null);
		else
			setValueNoCheck(COLUMNNAME_AD_Process_ID, Integer.valueOf(AD_Process_ID));
	}

	/**
	 * Get Process.
	 * 
	 * @return Process or Report
	 */
	public int getAD_Process_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_Process_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * Set AD_Process_UU.
	 * 
	 * @param AD_Process_UU AD_Process_UU
	 */
	public void setAD_Process_UU(String AD_Process_UU) {
		setValueNoCheck(COLUMNNAME_AD_Process_UU, AD_Process_UU);
	}

	/**
	 * Get AD_Process_UU.
	 * 
	 * @return AD_Process_UU
	 */
	public String getAD_Process_UU() {
		return (String) getValueNoCheck(COLUMNNAME_AD_Process_UU);
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
	 * Set Comment/Help.
	 * 
	 * @param Help Comment or Hint
	 */
	public void setHelp(String Help) {
		setValueNoCheck(COLUMNNAME_Help, Help);
	}

	/**
	 * Get Comment/Help.
	 * 
	 * @return Comment or Hint
	 */
	public String getHelp() {
		return (String) getValueNoCheck(COLUMNNAME_Help);
	}

	/**
	 * Set Procedure.
	 * 
	 * @param ProcedureName Name of the Database Procedure
	 */
	public void setProcedureName(String ProcedureName) {
		setValueNoCheck(COLUMNNAME_ProcedureName, ProcedureName);
	}

	/**
	 * Get Procedure.
	 * 
	 * @return Name of the Database Procedure
	 */
	public String getProcedureName() {
		return (String) getValueNoCheck(COLUMNNAME_ProcedureName);
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

	public int getAD_Scripting_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_Scripting_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

}
