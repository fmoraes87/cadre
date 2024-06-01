package com.cadre.server.core.entity;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MApp.TABLE_NAME)
public class MApp extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "AD_App";
	public static final int TABLE_ID = 52;

	/** Column name AD_App_ID */
	public static final String COLUMNNAME_AD_App_ID = "AD_App_ID";

	/** Column name Description */
	public static final String COLUMNNAME_Description = "Description";
	/** Column name Value */
	public static final String COLUMNNAME_Value = "Value";



}
