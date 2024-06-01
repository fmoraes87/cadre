package com.cadre.server.core.entity;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MModelValidator.TABLE_NAME)
public class MModelValidator extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** TableName=AD_ModelValidator */
	public static final String TABLE_NAME = "AD_ModelValidator";
	/** AD_Table_ID=24 */
	public static final int TABLE_ID = 24;

	/** Column name AD_ModelValidator_ID */
	public static final String COLUMNNAME_AD_ModelValidator_ID = "AD_ModelValidator_ID";

	/** Column name AD_ModelValidator_UU */
	public static final String COLUMNNAME_AD_ModelValidator_UU = "AD_ModelValidator_UU";
	
	/** Column name AD_Table_ID */
	public static final String COLUMNNAME_AD_Table_ID = "AD_Table_ID";

	/** Column name Description */
	public static final String COLUMNNAME_Description = "Description";

	/** Column name Help */
	public static final String COLUMNNAME_Help = "Help";

	/** Column name ModelValidationClass */
	public static final String COLUMNNAME_ModelValidationClass = "ModelValidationClass";

	/** Column name Name */
	public static final String COLUMNNAME_Name = "Name";

	/** Column name SeqNo */
	public static final String COLUMNNAME_SeqNo = "SeqNo";

	/** Column name AD_Extension_ID */
	public static final String COLUMNNAME_AD_Extension_ID = "AD_Extension_ID";
	
	/**
	 * Set Model Validation Class.
	 * 
	 * @param ModelValidationClass Model Validation Class
	 */
	public void setModelValidationClass(String ModelValidationClass) {
		setValueNoCheck(COLUMNNAME_ModelValidationClass, ModelValidationClass);
	}

	/**
	 * Get Model Validation Class.
	 * 
	 * @return Model Validation Class
	 */
	public String getModelValidationClass() {
		return (String) getValueNoCheck(COLUMNNAME_ModelValidationClass);
	}
}
