package com.cadre.server.core.util;

import javax.enterprise.inject.spi.CDI;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.cache.CCache;
import com.cadre.server.core.entity.MMessage;
import com.cadre.server.core.persistence.exception.DBNoResultException;
import com.cadre.server.core.resolver.DynamicServiceResolver;
import com.cadre.server.core.service.ModelService;

public class MessageUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageUtils.class);


	/**	Cache					*/
	static private CCache<String,String> s_cache = new CCache<>(MMessage.TABLE_NAME, 100);

	
	public static String parseMessage(final String message) {
		String result = message;
		
		//doing this validation twice to avoid search for message that isn't variable
		if (isVariable(message))
		{			
			String key = parseVariable(message);
			result = getMessageValue(key);	
		}

		return result;
	}
	
	public static boolean isVariable(String value) {
		return StringUtils.isNotEmpty(value)
				 && value.startsWith("@")
				 && value.endsWith("@")
				 && value.length() > 2 ;
	}
	
	public static String parseVariable(String value) {
		if (isVariable(value))
		{
			
			String key = value.substring(1,value.length()-1);
			return key;
			
		}else {
			return value;
		}
	}
	
	public static String getMessageValue(String messageKey) {
		if (StringUtils.isEmpty(messageKey)) {
			return StringUtils.EMPTY; 
		}
		
		String finalKey = CadreEnv.getAD_Language()+"_"+messageKey;
		String retValue = s_cache.get(finalKey);
		if (retValue == null)
		{
			try {
				ModelService service = CDI.current().select(ModelService.class).get();
				MMessage messagePO = service.getPO(null, MMessage.TABLE_NAME, MMessage.COLUMNNAME_Value,messageKey);
				String result = messagePO.getMsgText();
				s_cache.put(finalKey, result);			
				
				return result;
				
			}catch (DBNoResultException ex) {
				LOGGER.error("getMessageValue(messageKey="+messageKey+")", ex);

				return messageKey;
			}
		}else {
			return retValue;
		}
	}
}
