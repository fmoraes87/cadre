package com.cadre.server.core.entity;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cadre.server.core.annotation.CadreModel;
import com.cadre.server.core.util.PropertiesUtils;

@CadreModel(MServiceProvider.TABLE_NAME)
public class MServiceProvider extends POModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** TableName=AD_ServiceProvider */
	public static final String TABLE_NAME = "AD_ServiceProvider";
	/** AD_Table_ID= */
	public static final int TABLE_ID = 27;

	/** Column name AD_ServiceProvider_ID */
	public static final String COLUMNNAME_AD_ServiceProvider_ID = "AD_ServiceProvider_ID";

	/** Column name AD_ServiceProvider_UU */
	public static final String COLUMNNAME_AD_ServiceProvider_UU = "AD_ServiceProvider_UU";
	
	/** Column name Value */
	public static final String COLUMNNAME_Value = "Value";
	
	/** Column name Attributes */
	public static final String COLUMNNAME_Attributes = "Attributes";
	
	/** Column name Attributes */
	public static final String COLUMNNAME_ClassName = "Classname";
	
	/** Column name Attributes */
	public static final String COLUMNNAME_ServiceType = "ServiceType";

	public String getAttributes() {
		return (String) getValueNoCheck(COLUMNNAME_Attributes);
	}
	
	public String getAttributeValue(String paramName) {
		String attributes = getAttributes();
		if (StringUtils.isNoneEmpty(attributes)) {
			Map<String, String> values = PropertiesUtils.parseMap(attributes);
			return values.get(paramName);
		}else {
			return StringUtils.EMPTY;	
		}
	}
	
	public Map<String, String> getAttributesAsMap() {
		String attributes = getAttributes();
		if (StringUtils.isNoneEmpty(attributes)) {
			return PropertiesUtils.parseMap(attributes);
			
		}else {
			return Collections.EMPTY_MAP;
		}
	}
	


}
