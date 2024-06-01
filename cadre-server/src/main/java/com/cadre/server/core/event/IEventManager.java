package com.cadre.server.core.event;

public interface IEventManager {

	public static final String EVENT_FILTER = "event.filter";
	public static final String EVENT_TOPIC = "event.topic";
	public static final String EVENT_DATA = "event.data";
	public static final String EVENT_PROPERTIES = "event.properties";
	public static final String EVENT_ERROR_MESSAGES = "event.errorMessages";
	
	/**
	 * register a new event handler
	 * @param topic
	 * @param eventHandler
	 * @return true if registration is successful, false otherwise
	 */
	public abstract boolean register(String topic, EventHandler eventHandler);
	
	/**
	 * register a new event handler
	 * @param topic
	 * @param filter
	 * @param eventHandler
	 * @return true if registration is successful, false otherwise
	 */
	public abstract boolean register(String topic, String filter,EventHandler eventHandler);
	
	
	/**
	 * Initiate asynchronous delivery of an event. This method returns to the
	 *  caller before delivery of the event is completed.
	 * @param event
	 * @return
	 */
	public abstract boolean postEvent(CEvent event);
	
	/**
	 * Initiate synchronous delivery of an event. This method does not return to
	 * the caller until delivery of the event is completed.
	 *
	 * @param event The event to send to all listeners which subscribe to the
	 *        topic of the event.
	 *
	 * @throws SecurityException If the caller does not have
	 *         <code>TopicPermission[topic,PUBLISH]</code> for the topic
	 *         specified in the event.
	 */
	public abstract boolean sendEvent(CEvent event);
	
}
