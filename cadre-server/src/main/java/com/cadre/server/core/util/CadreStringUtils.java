package com.cadre.server.core.util;

import org.apache.commons.lang3.StringUtils;

public class CadreStringUtils {

	public static final char apostrophe = 0x2019;
	public static final char singleQuote=0x27;
	
	public static String replaceSingleQuote(String input) {
		if (StringUtils.isNotBlank(input)) {
			return input.replace(singleQuote, apostrophe);			
		}
		
		return input;
	}
}
