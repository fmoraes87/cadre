package com.cadre.server.core.entity;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MMediaFormat.TABLE_NAME)
public class MMediaFormat extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** TableName=AD_MediaFormat */
	public static final String TABLE_NAME = "AD_MediaFormat";
	/** AD_Table_ID= */
	public static final int TABLE_ID = 29;
	  /** Column name AD_Message_ID */
    public static final String COLUMNNAME_AD_MediaFormat_ID = "AD_MediaFormat_ID";
    /** Column name MsgType */
    public static final String COLUMNNAME_Extension = "Extension";
    /** Column name MsgType */
    public static final String COLUMNNAME_MimeType = "MimeType";
    /** Column name MsgType */
    public static final String COLUMNNAME_Description = "Description";
    
	
	
	/**
	 * 
	 * @return
	 */
	public int getAD_MediaFormat_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_MediaFormat_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}
	
}
