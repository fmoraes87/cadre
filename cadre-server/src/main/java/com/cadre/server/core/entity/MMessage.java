package com.cadre.server.core.entity;

import java.util.concurrent.Callable;

import com.cadre.server.core.annotation.CadreModel;

@CadreModel(MMessage.TABLE_NAME)
public class MMessage extends POModel {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** TableName=AD_Message */
	public static final String TABLE_NAME = "AD_Message";
	/** AD_Table_ID= */
	public static final int TABLE_ID = 10;
	  /** Column name AD_Message_ID */
    public static final String COLUMNNAME_AD_Message_ID = "AD_Message_ID";
    /** Column name AD_Message_UU */
    public static final String COLUMNNAME_AD_Message_UU = "AD_Message_UU";
    /** Column name MsgText */
    public static final String COLUMNNAME_MsgText = "MsgText";
    /** Column name MsgTip */
    public static final String COLUMNNAME_MsgTip = "MsgTip";
    /** Column name MsgType */
    public static final String COLUMNNAME_MsgType = "MsgType";
    /** Column name Value */
    public static final String COLUMNNAME_Value = "Value";
    

	public MMessage() {
		addGetActionListener(COLUMNNAME_MsgText, new Callable<String>() {
			public String call() {
				return getMsgText();
			}
		});
	}

	/**
	 * Get Message Text.
	 * 
	 * @return Textual Informational, Menu or Error Message
	 */
	public String getMsgText () 
	{
		return (String)getValueNoCheck(COLUMNNAME_MsgText);
	}

	
}
