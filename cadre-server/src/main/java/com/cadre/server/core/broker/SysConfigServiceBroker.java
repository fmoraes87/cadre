package com.cadre.server.core.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.cache.CCache;
import com.cadre.server.core.entity.MSysConfig;
import com.cadre.server.core.persistence.jdbc.RDBMS;

//@Singleton
public class SysConfigServiceBroker {

	private static final Logger LOGGER = LoggerFactory.getLogger(SysConfigServiceBroker.class);

	
	/** Cache			*/
	private static CCache<String, String> s_cache = new CCache<String, String>(MSysConfig.Table_Name, 40, 0, true);
	
	
	public SysConfigServiceBroker() {	}
	
	/**
	 * Get system configuration property of type string
	 * @param Name
	 * @param defaultValue
	 * @return String
	 */
	public static String getValue(String Name, String defaultValue)
	{
		return getValue(Name, defaultValue, 0, 0);
	}
	
	/**
	 * Get system configuration property of type string
	 * @param Name
	 * @return String
	 */
	public static String getValue(String Name)
	{
		return getValue(Name, null);
	}
	
	
	/**
	 * Get system configuration property of type int
	 * @param Name
	 * @param defaultValue
	 * @return int
	 */
	public static int getIntValue(String Name, int defaultValue)
	{
		String s = getValue(Name);
		if (s == null)
			return defaultValue; 
		
		if (s.length() == 0)
			return defaultValue;
		//
		try
		{
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			LOGGER.error("getIntValue (" + Name + ") = " + s, e);
		}
		return defaultValue;
	}
	
	/**
	 * Get system configuration property of type double
	 * @param Name
	 * @param defaultValue
	 * @return double
	 */
	public static double getDoubleValue(String Name, double defaultValue)
	{
		String s = getValue(Name);
		if (s == null || s.length() == 0)
			return defaultValue;
		//
		try
		{
			return Double.parseDouble(s);
		}
		catch (NumberFormatException e)
		{
			LOGGER.error("getDoubleValue (" + Name + ") = " + s, e);
		}
		return defaultValue;
	}
	
	/**
	 * Get system configuration property of type boolean
	 * @param Name
	 * @param defaultValue
	 * @return boolean
	 */
	public static boolean getBooleanValue(String Name, boolean defaultValue)
	{
		String s = getValue(Name);
		if (s == null || s.length() == 0)
			return defaultValue;
		
		if ("Y".equalsIgnoreCase(s))
			return true;
		else if ("N".equalsIgnoreCase(s))
			return false;
		else
			return Boolean.valueOf(s).booleanValue();
	}
	
	/**
	 * Get client configuration property of type string
	 * @param Name
	 * @param defaultValue
	 * @param Client ID
	 * @return String
	 */
	public static String getValue(String Name, String defaultValue, int AD_Client_ID)
	{
		return getValue(Name, defaultValue, AD_Client_ID, 0);
	}
	
	/**
	 * Get system configuration property of type string
	 * @param Name
	 * @param Client ID
	 * @return String
	 */
	public static String getValue(String Name, int AD_Client_ID)
	{
		return (getValue(Name, null, AD_Client_ID));
	}
	
	/**
	 * Get system configuration property of type int
	 * @param Name
	 * @param defaultValue
	 * @param Client ID
	 * @return int
	 */
	public static int getIntValue(String Name, int defaultValue, int AD_Client_ID)
	{
		String s = getValue(Name, AD_Client_ID);
		if (s == null)
			return defaultValue; 
		
		if (s.length() == 0)
			return defaultValue;
		//
		try
		{
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			LOGGER.error("getIntValue (" + Name + ") = " + s, e);
		}
		return defaultValue;
	}
	
	/**
	 * Get system configuration property of type double
	 * @param Name
	 * @param defaultValue
	 * @param Client ID
	 * @return double
	 */
	public static double getDoubleValue(String Name, double defaultValue, int AD_Client_ID)
	{
		String s = getValue(Name, AD_Client_ID);
		if (s == null || s.length() == 0)
			return defaultValue;
		//
		try
		{
			return Double.parseDouble(s);
		}
		catch (NumberFormatException e)
		{
			LOGGER.error("getDoubleValue (" + Name + ") = " + s, e);
		}
		return defaultValue;
	}
	
	/**
	 * Get system configuration property of type boolean
	 * @param Name
	 * @param defaultValue
	 * @param Client ID
	 * @return boolean
	 */
	public static boolean getBooleanValue(String Name, boolean defaultValue, int AD_Client_ID)
	{
		String s = getValue(Name, AD_Client_ID);
		if (s == null || s.length() == 0)
			return defaultValue;
		
		if ("Y".equalsIgnoreCase(s))
			return true;
		else if ("N".equalsIgnoreCase(s))
			return false;
		else
			return Boolean.valueOf(s).booleanValue();
	}

	/**
	 * Get client configuration property of type string
	 * @param Name
	 * @param defaultValue
	 * @param Client ID
	 * @param Organization ID
	 * @return String
	 */
	public static String getValue(String Name, String defaultValue, int AD_Client_ID, int AD_Org_ID)
	{
		String key = ""+AD_Client_ID+"_"+AD_Org_ID+"_"+Name;
		String str = s_cache.get(key);
		if (str != null)
			return str;
		if (str == null && s_cache.containsKey(key)) // found null key
			return defaultValue;
		
		String sql = "SELECT Value FROM AD_SysConfig"
				+ " WHERE Name=? AND AD_Client_ID IN (0, ?) AND AD_Org_ID IN (0, ?) AND IsActive='Y'"
				+ " ORDER BY AD_Client_ID DESC, AD_Org_ID DESC";
		
		
		str = RDBMS.getSQLValueStringEx(null, sql, Name,AD_Client_ID,AD_Org_ID);
		
		//
		if (str != null) {
			s_cache.put(key, str);
			return str;
		}
		else {
			// anyways, put the not found key as null
			s_cache.put(key, null);
			return defaultValue;
		}
	}
	
	/**
	 * Get system configuration property of type string
	 * @param Name
	 * @param Client ID
	 * @param Organization ID
	 * @return String
	 */
	public static String getValue(String Name, int AD_Client_ID, int AD_Org_ID)
	{
		return getValue(Name, null, AD_Client_ID, AD_Org_ID);
	}
	
	/**
	 * Get system configuration property of type int
	 * @param Name
	 * @param defaultValue
	 * @param Client ID
	 * @param Organization ID
	 * @return int
	 */
	public static int getIntValue(String Name, int defaultValue, int AD_Client_ID, int AD_Org_ID)
	{
		String s = getValue(Name, AD_Client_ID, AD_Org_ID);
		if (s == null)
			return defaultValue; 
		
		if (s.length() == 0)
			return defaultValue;
		//
		try
		{
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			LOGGER.error("getIntValue (" + Name + ") = " + s, e);
		}
		return defaultValue;
	}
	
	/**
	 * Get system configuration property of type double
	 * @param Name
	 * @param defaultValue
	 * @param Client ID
	 * @param Organization ID
	 * @return double
	 */
	public static double getDoubleValue(String Name, double defaultValue, int AD_Client_ID, int AD_Org_ID)
	{
		String s = getValue(Name, AD_Client_ID, AD_Org_ID);
		if (s == null || s.length() == 0)
			return defaultValue;
		//
		try
		{
			return Double.parseDouble(s);
		}
		catch (NumberFormatException e)
		{
			LOGGER.error("getDoubleValue (" + Name + ") = " + s, e);

		}
		return defaultValue;
	}
	
	/**
	 * Get system configuration property of type boolean
	 * @param Name
	 * @param defaultValue
	 * @param Client ID
	 * @param Organization ID
	 * @return boolean
	 */
	public static boolean getBooleanValue(String Name, boolean defaultValue, int AD_Client_ID, int AD_Org_ID)
	{
		String s = getValue(Name, AD_Client_ID, AD_Org_ID);
		if (s == null || s.length() == 0)
			return defaultValue;
		
		if ("Y".equalsIgnoreCase(s))
			return true;
		else if ("N".equalsIgnoreCase(s))
			return false;
		else
			return Boolean.valueOf(s).booleanValue();
	}


}
