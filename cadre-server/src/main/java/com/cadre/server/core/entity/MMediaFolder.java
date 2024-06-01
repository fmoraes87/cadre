package com.cadre.server.core.entity;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cadre.server.core.annotation.CadreModel;
import com.cadre.server.core.util.PropertiesUtils;

@CadreModel(MMediaFolder.TABLE_NAME)
public class MMediaFolder extends POModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** TableName=AD_MediaFolder */
	public static final String TABLE_NAME = "AD_MediaFolder";
	/** AD_Table_ID= */
	public static final int TABLE_ID = 30;
	
	  /** Column name AD_MediaFolder_ID */
    public static final String COLUMNNAME_AD_MediaFolder_ID = "AD_MediaFolder_ID";
    /** Column name Name */
    public static final String COLUMNNAME_Name = "Name";
    /** Column name Method */
    public static final String COLUMNNAME_Method = "Method";
    /** Column name Method */
    public static final String COLUMNNAME_Attributes = "Attributes";
	/** Column name IsInternalStorage */
	public static final String COLUMNNAME_IsActive = "IsInternalStorage";
	/** Column name IsSecurityAccess */
	public static final String COLUMNNAME_IsSecurityAccess = "IsSecurityAccess";
	
	public MMediaFolder() {
	
	}
	
	public String getName() {
		return (String) getValueOfColumn(COLUMNNAME_Name);
	}
	
	public String getAttributes() {
		return (String) getValueOfColumn(COLUMNNAME_Attributes);
	}
	
	
	public Map<String, String> getAttributesAsMap() {
		String attributes = getAttributes();
		if (StringUtils.isNoneEmpty(attributes)) {
			return PropertiesUtils.parseMap(attributes);
			
		}else {
			return Collections.EMPTY_MAP;
		}
	}
	

	/**
	 * Get Method
	 * @return
	 */
	public String getMethod () 
	{
		return (String)getValueNoCheck(COLUMNNAME_Method);
	}

	
	/**
	 * 
	 * @return
	 */
	public int getAD_MediaFolder_ID() {
		Integer ii = (Integer) getValueNoCheck(COLUMNNAME_AD_MediaFolder_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}
}
