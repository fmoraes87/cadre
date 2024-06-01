package com.cadre.server.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtils.class);


	public static int getIntProperty(Properties properties, String key, int defaultValue) {
		int i = defaultValue;
		String s = properties.getProperty(key);
		try {
				i = Integer.parseInt(s);
		} catch (Exception e) {
			LOGGER.error("getIntValue (" + key + ") = " + s, e);

		}
		return i;
	}

	public static boolean getBooleanProperty(Properties properties, String key, boolean defaultValue) {
		boolean b = defaultValue;
		String s = properties.getProperty(key);
		try {
			if (s != null && s.trim().length() > 0)
				b = Boolean.valueOf(s);
		} catch (Exception e) {
			LOGGER.error("getBooleanProperty (" + key + ") = " + s, e);

		}
		return b;
	}

	public static String getStringProperty(Properties properties, String key, String defaultValue) {
		String b = defaultValue;
		String s = properties.getProperty(key);
		try {
			if (s != null && s.trim().length() > 0)
				b = s.trim();
		} catch (Exception e) {
			LOGGER.error("getStringProperty (" + key + ") = " + s, e);

		}
		return b;
	}
	
	public static Map<String, String> parseMap(final String input) {
        final Map<String, String> map = new HashMap<String, String>();
        for (String pair : input.split(";")) {
            String[] kv = pair.split(":");
            map.put(kv[0], kv[1]);
        }
        return map;
    }

}
