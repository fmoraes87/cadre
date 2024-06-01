package com.cadre.server.core;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.persistence.exception.DBException;
import com.cadre.server.core.persistence.jdbc.RDBMS;
import com.cadre.server.core.util.SecurityUtils;

public class CadreEnv {

	private static final Logger LOGGER = LoggerFactory.getLogger(CadreEnv.class);


	private static final String CONFIG_VARS_ENV_PROD = "PROD";

	private static final String CONFIG_VARS_ENV = "ENV";

	private static final String MSG_REQUIRE_CONTEXT = "Require Context";

	/** Base Language               */
	private static final String  DEFAULT_LANGUAGE = "en_US";
	private static final String SQL_DEFAULT_LANGUAGE = "SELECT AD_Language FROM AD_Language WHERE IsActive='Y' AND IsBaseLanguage = 'Y'";
	
	/** Context identifier */
	public static final String BASE_LANGUAGE = "#Base_Language";
	public static final String LANGUAGE = "#AD_Language";
	public static final String AD_USER_ID = "#AD_User_ID";
	public static final String AD_ORG_ID = "#AD_Org_ID";
	public static final String AD_CLIENT_ID = "#AD_Client_ID";
	public static final String AD_App_ID = "#AD_App_ID";
	public static final String AD_OAuth2_Client_ID = "#AD_OAuth2_Client_ID";
	public static final String AD_USER_ACCESS_LEVEL = "#AD_USER_ACCESS_LEVEL";
	public static final String TRX_NAME = "#LOCAL_TRX_NAME";
	public static final String ONLY_ACTIVE_RECORDS = "#IsViewOnlyActiveRecords";



	private static final ThreadLocal<Properties> threadLocal;

	// Initialize current thread
	static {
		threadLocal = new ThreadLocal<>();
	}

	/**
	 * Set current context
	 * 
	 * @param context
	 */
	public static synchronized void setCtx(Properties context) {
		threadLocal.set(context);
	}

	/**
	 * Get Current Context
	 * 
	 * @return
	 */
	public static synchronized Properties getCtx() {
		return threadLocal.get();
	}

	/**
	 * Clean current config.
	 */
	public static void clean() {
		threadLocal.remove();

	}

	/**
	 * Check Base Language
	 * 
	 * @param ctx       context
	 * @param tableName table to be translated
	 * @return true if base language and table not translated
	 */
	public static boolean isBaseLanguageSelected() {
		return getBaseLanguage().equals(getAD_Language());
	} // isBaseLanguage

	
	/**
	 * Set Context value
	 * 
	 * @param propertie
	 * @param value
	 */
	public static void setContextValue(String propertie, String value) {
		Properties ctx = getCtx();

		if (ctx == null || propertie == null) {
			return;
		}
		//
		if (value == null || value.length() == 0) {
			ctx.remove(propertie);
		} else {
			ctx.setProperty(propertie, value);
		}

	}
	
	 /*	Set Global Context to Y/N Value
	 *  @param ctx context
	 *  @param context context key
	 *  @param value context value
	 */
	public static void setContextValue (String context, boolean value)
	{
		setContextValue (context, value ? POModel.YES_VALUE : POModel.NO_VALUE);
	}	//	setContext

	 
	 

	/**
	 *	Get global Value of Context
	 *  @param ctx context
	 *  @param context context key
	 *  @return value or ""
	 */
	public static String getContext (Properties ctx, String context)
	{
		if (ctx == null || context == null) {
			throw new IllegalArgumentException (MSG_REQUIRE_CONTEXT);			
		}
		return ctx.getProperty(context, StringUtils.EMPTY);
	}	//	getContext

	
	/**
	 * Get System AD_Language
	 * 
	 * @param ctx context
	 * @return AD_Language eg. en_US
	 */
	public static String getAD_Language() {

		String lang = getContext(getCtx(), LANGUAGE);
		if (!StringUtils.isEmpty(lang)) {
			return lang;			
		}else {
			lang = getBaseLanguage();

		}
		return lang;

	} // getAD_Language

	/**
	 *  Get Base Language
	 *  @return String Language
	 */
	private static String getBaseLanguage() {
		String baseLang = getContext(getCtx(), BASE_LANGUAGE);
		if (!StringUtils.isEmpty(baseLang)) {
			return baseLang;			
		}else {
			try {
				baseLang = RDBMS.getSQLValueStringEx(null, SQL_DEFAULT_LANGUAGE);				
			}catch(DBException ex) {
				LOGGER.error("getBaseLanguage()",ex);
				baseLang = DEFAULT_LANGUAGE;
			}
			
			getCtx().setProperty(BASE_LANGUAGE, baseLang);
			return baseLang;
		}

	} //getBaseLanguage

	/**
	 * Set Global Context to (int) Value
	 * 
	 * @param ctx     context
	 * @param context context key
	 * @param value   context value
	 */
	public static void setContextValue(String context, int value) {
		Properties ctx = getCtx();
		if (ctx == null || context == null) {
			return;			
		}
		//
		ctx.setProperty(context, String.valueOf(value));
	} // setContext

	public static int getAD_Client_ID() {
		return getContextAsInt(AD_CLIENT_ID);			

	} //getAD_Client_ID

	public static int getAD_User_ID() {
		return getContextAsInt(AD_USER_ID);			

	} //getAD_User_ID

	public static int getAD_Org_ID() {
		return getContextAsInt(AD_ORG_ID);			

	} //getAD_Org_ID
	
	public static int getAD_OAuth2_Client_ID() {
		return getContextAsInt(AD_OAuth2_Client_ID);			

	} //getAD_OAuth2_Client_ID
	
	
	public static int get_AD_App_ID() {
		return getContextAsInt(AD_App_ID);			

	} //getAD_OAuth2_Client_ID
	
	
	/**
	 *	Get Context and convert it to an integer (-1 if error)
	 *  @param ctx context
	 *  @param context context key
	 *  @return value
	 */
	public static int getContextAsInt(String context)
	{
		if (getCtx() == null || context == null) {
			throw new IllegalArgumentException (MSG_REQUIRE_CONTEXT);
		}
		
		String s = getCtx().getProperty(context);
		if (StringUtils.isEmpty(s)) {
			return 0;
		}
		//
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			LOGGER.error("getContextAsInt(context="+context+")",e);
		}
		
		
		return 0;
	}	//	getContextAsInt

    /**
     * Retrieve the value of trxName.
     *
     * @return the trxName
     */
    public static String getTrxName() {
        return getContext(getCtx(), TRX_NAME);
    }

	public static boolean isProduction() {
		String value = System.getenv(CONFIG_VARS_ENV);
		if (StringUtils.isNoneEmpty(value) &&
				value.equalsIgnoreCase(CONFIG_VARS_ENV_PROD)) {
			return true;
			
		}else {
			return false;
		}
	}

	public static String getGrantType() {
        return getContext(getCtx(), SecurityUtils.GRANT_TYPE);

	}
	
	/**
	 *	Is Sales Order Trx
	 *  @param ctx context
	 *  @param WindowNo window no
	 *  @return true if SO (default)
	 */
	public static boolean isViewOnlyActiveRecords ()
	{
		String s = getContext(getCtx(),ONLY_ACTIVE_RECORDS);
		if (s != null && s.equals(POModel.YES_VALUE)) {
			return true;			
		}
		return false;
	}	//	isSOTrx

	public static void main (String [] args) {
		String name=null;
		if (name.equals(null)) {
			System.out.println("teste");
			
		}
	}
    
}
