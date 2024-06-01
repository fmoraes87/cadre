package com.cadre.server.core.entity;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MExtension.TABLE_NAME)
public class MExtension extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** TableName=AD_Extension */
	public static final String TABLE_NAME = "AD_Extension";
	/** AD_Table_ID= */
	public static final int TABLE_ID = 26;

	/** Column name AD_ModelValidator_ID */
	public static final String COLUMNNAME_AD_Extension_ID = "AD_Extension_ID";

	/** Column name AD_ModelValidator_UU */
	public static final String COLUMNNAME_AD_ModelValidator_UU = "AD_Extension_UU";
	
	/** Column name Name */
	public static final String COLUMNNAME_Name = "Name";
	
	/** Column name Description */
	public static final String COLUMNNAME_Description = "Description";

	/** Column name ServiceProviderClass */
	public static final String COLUMNNAME_ServiceProviderClass = "ServiceProviderClass";
	
	/** Column name ModelProviderClass */
	public static final String COLUMNNAME_ModelProviderClass = "ModelProviderClass";

	/** Column name SeqNo */
	public static final String COLUMNNAME_SeqNo = "SeqNo";

	public String getModelProviderClass() {
		return (String) getValueNoCheck(COLUMNNAME_ModelProviderClass);
	}
	
	public String getServiceProviderClass() {
		return (String) getValueNoCheck(COLUMNNAME_ServiceProviderClass);
	}

}
