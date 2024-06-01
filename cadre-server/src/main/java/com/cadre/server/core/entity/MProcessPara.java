package com.cadre.server.core.entity;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MProcessPara.TABLE_NAME)
public class MProcessPara extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** TableName=AD_Process */
	public static final String TABLE_NAME = "AD_Process_Para";
	/** AD_Table_ID= */
	public static final int TABLE_ID = 56;
	/** Column name AD_Process_Para_ID */
	public static final String COLUMNNAME_AD_Process_Para_ID = "AD_Process_Para_ID";
	/** Column name AD_Process_Para_UU */
	public static final String COLUMNNAME_AD_Process_Para_UU = "AD_Process_Para_UU";
	/** Column name DefaultValue */
	public static final String COLUMNNAME_DefaultValue = "DefaultValue";
	/** Column name Description */
	public static final String COLUMNNAME_Description = "Description";
	/** Column name Help */
	public static final String COLUMNNAME_Help = "Help";
	/** Column name ColumnName */
	public static final String COLUMNNAME_ColumnName = "ColumnName";
	/** Column name IsMandatory */
	public static final String COLUMNNAME_IsMandatory = "IsMandatory";
	/** Column name IsSameLine */
	public static final String COLUMNNAME_IsSameLine = "IsSameLine";
	/** Column name Label */
	public static final String COLUMNNAME_Label = "Label";
	/** Column name Placeholder */
	public static final String COLUMNNAME_Placeholder = "Placeholder";
	/** Column name SeqNo */
	public static final String COLUMNNAME_SeqNo = "SeqNo";
	/** Column name AD_Process_ID */
	public static final String COLUMNNAME_AD_Process_ID = "AD_Process_ID";
	/** Column name AD_Reference_ID */
	public static final String COLUMNNAME_AD_Reference_ID = "AD_Reference_ID";
	/** Column name AD_Reference_Value_ID */
	public static final String COLUMNNAME_AD_Reference_Value_ID = "AD_Reference_Value_ID";
	/** Column name BootstrapClass */
	public static final String COLUMNNAME_BootstrapClass = "BootstrapClass";
	/** Column name AD_Extension_ID */
	public static final String COLUMNNAME_AD_Extension_ID = "AD_Extension_ID";
	/** Column name DynamicValidation */
	public static final String COLUMNNAME_DynamicValidation = "DynamicValidation";

	public MProcessPara() {

	}

	/**
	 * Get Process.
	 * 
	 * @return Process or Report
	 */
	public int getAD_Process_Para_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_Process_Para_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
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
	 * Get Reference.
	 * 
	 * @return System Reference and Validation
	 */
	public int getAD_Reference_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_Reference_ID);
		if (ii == null) {
			return 0;
		}
		return ii.intValue();
	}
	
	/**
	 * Get Reference.
	 * 
	 * @return System Reference and Validation
	 */
	public int getAD_Reference_Value_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_Reference_Value_ID);
		if (ii == null) {
			return 0;
		}
		return ii.intValue();
	}

	public String getColumnName() {
		return (String) getValueNoCheck(COLUMNNAME_ColumnName);
	}


}
