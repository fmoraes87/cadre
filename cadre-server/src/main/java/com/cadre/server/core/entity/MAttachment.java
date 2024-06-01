package com.cadre.server.core.entity;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MAttachment.TABLE_NAME)
public class MAttachment extends POModel{

	/** TableName=AD_Media */
	public static final String TABLE_NAME = "AD_Attachment";
	/** AD_Table_ID= */
	public static final int TABLE_ID = 31;
	  /** Column name AD_Attachment_ID */
    public static final String COLUMNNAME_AD_Attachment_ID = "AD_Attachment_ID";
	  /** Column name AD_Media_ID */
    public static final String COLUMNNAME_AD_Media_ID = "AD_Media_ID";
	  /** Column name AD_Table_ID */
    public static final String COLUMNNAME_AD_Table_ID = "AD_Table_ID";
	  /** Column name AD_Record_ID */
    public static final String COLUMNNAME_AD_Record_ID = "AD_Record_ID";
}
