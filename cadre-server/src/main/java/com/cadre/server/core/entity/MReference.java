package com.cadre.server.core.entity;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MReference.TABLE_NAME)
public class MReference extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "AD_Reference";
	public static final int TABLE_ID = 6;

	/** Column name AD_Reference_ID */
	public static final String COLUMNNAME_AD_Reference_ID = "AD_Reference_ID";
	/** Column name AD_Reference_UU */
	public static final String COLUMNNAME_AD_Reference_UU = "AD_Reference_UU";
	/** Column name Name */
	public static final String COLUMNNAME_Name = "Name";
	/** Column name Description */
	public static final String COLUMNNAME_Description = "Description";	
	/** Column name ValidationType */
	public static final String COLUMNNAME_ValidationType = "ValidationType";
	/** Column name Help */
	public static final String COLUMNNAME_Help = "Help";
	/** Column name IsOrderByValue */
	public static final String COLUMNNAME_IsOrderByValue = "IsOrderByValue";
	/** Column name ReferenceValue */
	public static final String COLUMNNAME_AD_Table_ID = "AD_Table_ID";
	/** Column name AD_Extension_ID */
	public static final String COLUMNNAME_AD_Extension_ID = "AD_Extension_ID";
	
	public MReference() {
	}
	
	/**
	 * Get AD_Table_ID.
	 * 
	 * @return
	 */
	public int getAD_Table_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_Table_ID);
		if (ii == null) {
			return 0;
		}
		return ii.intValue();
	}


	
	
	/**
	 * Get ValidationType
	 * 
	 * @return 
	 */
	public String getValidationType() {
		return (String) getValueNoCheck(COLUMNNAME_ValidationType);
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public boolean isOrderByValue() {
		Object oo = getValueNoCheck(COLUMNNAME_IsOrderByValue);
		if (oo != null) {
			if (oo instanceof Boolean) {
				return ((Boolean) oo).booleanValue();
			}
			return POModel.YES_VALUE.equals(oo);
		}
		return false;
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
	 * Set AD_Extension_ID
	 * 
	 * @param AD_Extension_ID
	 */
	public void setAD_Extension_ID(Integer p_AD_Extension_ID) {
		setValueNoCheck(COLUMNNAME_AD_Extension_ID, p_AD_Extension_ID);
	}
	


}
