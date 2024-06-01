package com.cadre.server.core.event;

import java.util.Map;
import java.util.Set;

public class CEvent {

	private String topic;
	private String qualifier;
	private Map<String, Object> properties;

	public CEvent(String topic, Map<String, Object> map) {
		this(topic,null,map);
	}
	
	public CEvent(String topic, String qualifier, Map<String, Object> map) {
		this.topic = topic;
		this.qualifier = qualifier;
		this.properties = map;
	}

	@SuppressWarnings("unchecked")
	public <T> T getProperty(String property) {
		return (T) properties.get(property);
	}
	
	public String [] getPropertyNames() {
		Set<String> keySet = properties.keySet();
		String[] propertiesName = new String[keySet.size()];
		keySet.toArray(propertiesName);
	      
		return propertiesName;
	}

	public String getTopic() {
		return topic;
	}

	public String getQualifier() {
		return qualifier;
	}


	

}
