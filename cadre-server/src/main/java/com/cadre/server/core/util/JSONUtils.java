package com.cadre.server.core.util;

import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.json.stream.JsonParser;

import com.cadre.server.core.entity.POModel;

public class JSONUtils {

	public static Object convertContentValue(Object value) {
		if (value instanceof JsonValue) {
			JsonValue val = (JsonValue) value;
			Object ret = null;
			ValueType typ = val.getValueType();
			if (typ == ValueType.NUMBER)
				ret = ((JsonNumber)val).bigDecimalValue();
			else if (typ == ValueType.STRING)
				ret = ((JsonString)val).getString();
			else if (typ == ValueType.FALSE)
				ret = Boolean.FALSE;
			else if (typ == ValueType.TRUE)
				ret = Boolean.TRUE;
			else if (typ == ValueType.ARRAY) {
				JsonArray arr = (JsonArray)val;
				List<Object> vals = new ArrayList<Object>();
				int sz = arr.size();
				for (int i = 0; i < sz; i++) {
					JsonValue v = arr.get(i);
					vals.add(convertContentValue(v));
				}
				ret = vals;
			} else if (typ == ValueType.OBJECT) {
				//JsonObject obj = (JsonObject)val;
			}
			return ret;
		}
		return value;
	}

	public static Map<String, Object> getKeyValuesAsMap(String json) {
		final JsonParser parser = Json.createParser(new StringReader(json));
		parser.next(); // START_OBJECT
		
		Map<String,Object> model = parser.getObjectStream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> convertContentValue(entry.getValue())));
		
		return model;
		
	}

	public static JsonObject createJsonWith(POModel object, String... attributes) {
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
		if (object!=null && attributes!=null && attributes.length > 0) {
			Arrays.stream(attributes).forEach(attr -> {
				Object value = object.getValueOfColumn(attr);
				addJSONAttr(jsonBuilder, attr, value);	
			});
			
		}
		
		return jsonBuilder.build();
		
	}
	
	public static JsonObject createJsonWith(Map<String,Object> attributes) {
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
		if (attributes!=null) {
			attributes.entrySet().forEach(entry -> {
			
				String attr = entry.getKey();
			    Object value = entry.getValue();
			    
			    addJSONAttr(jsonBuilder, attr, value);
			});
		}
		
		return jsonBuilder.build();
		
	}

	private static void addJSONAttr(JsonObjectBuilder jsonBuilder, String attr, Object value) {
		Class<?> clazz = value.getClass();
		if (clazz == Integer.class) {
			jsonBuilder.add(attr, (Integer) value);
		} else if (clazz == BigDecimal.class) {
			jsonBuilder.add(attr, (BigDecimal) value);
		} else if (clazz == Boolean.class) {
			jsonBuilder.add(attr, Boolean.valueOf(value.toString()));
		} else if (clazz == Timestamp.class) {
			jsonBuilder.add(attr, value.toString());
		} else if (clazz == String.class) {
			jsonBuilder.add(attr, (String) value);
		} else {
			jsonBuilder.add(attr, value.toString());
		}
	}
	
}
