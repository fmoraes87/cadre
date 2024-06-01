package com.cadre.server.core.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class CEventManager implements IEventManager {

	/**	EventHandler Listeners for Event		*/
	private Map<Map<String, String>,Subject>	m_Topics = new Hashtable<>();
	
	
	 // Inner class to provide instance of class 
	private static class Singleton {
		private static final IEventManager INSTANCE = new CEventManager();
	}

	public static IEventManager getInstance() {
		return Singleton.INSTANCE;
	}	
	
	public static CEvent newEvent(String topic, Object data) {
		return newEvent(topic, null,data);
	}
	
	public static CEvent newEvent(String topic, String qualifier, Object data) {
		CEvent event = null;
		if (data instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>)data;
			if (!map.containsKey(EVENT_ERROR_MESSAGES)) {
				map.put(EVENT_ERROR_MESSAGES, new ArrayList<String>());				
			}
			
			event = new CEvent(topic,qualifier, map);
		} else {
			Map<String, Object> map = new HashMap<String, Object>(3);
			map.put(EVENT_TOPIC, topic);
			if (data != null) {
				map.put(EVENT_DATA, data);				
			}
			
			map.put(EVENT_ERROR_MESSAGES, new ArrayList<String>());
			event = new CEvent(topic, qualifier, map);
		}
		return event;
	}
	

	@Override
	public boolean postEvent(CEvent event) {
		CompletableFuture.runAsync(() -> {
			sendEvent(event);
		});
		
		return true;
	}

	public boolean sendEvent(CEvent event) {
		String topic = event.getTopic();
		String qualifier = event.getQualifier();
		Map<String, String> key = getIdentifier(topic, qualifier);
	
		Subject subject = m_Topics.get(key);
		if (subject!=null) {
			subject.notifyObservers(event);
		}
		return true;
	}

	//@Override
	public boolean register(String topic, EventHandler handler) {		
		return register(topic,null,handler);
	}

	@Override
	public boolean register(String topic, String filter, EventHandler handler) {
		if (StringUtils.isEmpty(topic)|| handler == null) {
			return false;			
		}

		Map<String, String> key = getIdentifier(topic, filter);
		
		Subject myTopic = m_Topics.get(key);

		if (myTopic == null) {
			myTopic = new Subject(topic);
			m_Topics.put(key, myTopic);
		}

		myTopic.addObserver(handler);
		
		return true;
	}

	private Map<String, String> getIdentifier(String topic, String filter) {
		Map<String, String> key = new Hashtable<>();
		key.put(EVENT_TOPIC, topic);
		
		if (StringUtils.isNotEmpty(filter)) {
			key.put(EVENT_FILTER, filter);
		}
		return key;
	}

}

class Subject{
	
	private final Object MUTEX= new Object();
	
	private String topic;
	private Set<EventHandler> observers;
	
	Subject(String topic) {
		this.topic=topic;
		this.observers = new HashSet<EventHandler>();
	}
	
    public void addObserver(EventHandler observer) {
    	if (observer!=null) {
    		synchronized (MUTEX) {
    			observers.add(observer);    			
    		}
    	}
	}
    
	public void unregister(EventHandler obj) {
		synchronized (MUTEX) {
			observers.remove(obj);
		}
	}
	
	public void notifyObservers(CEvent event) {
		List<EventHandler> observersLocal = null;
		//synchronization is used to make sure any observer registered after message is received is not notified
		synchronized (MUTEX) {
			observersLocal = new ArrayList<>(this.observers);
		}
		if (CollectionUtils.isNotEmpty(observersLocal)) {
			for (EventHandler obj : observersLocal) {
				obj.handleEvent(event);
			}
		}

	}

	public String getTopic() {
		return topic;
	}
	

}
