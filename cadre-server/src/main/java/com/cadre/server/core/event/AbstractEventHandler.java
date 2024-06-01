package com.cadre.server.core.event;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.entity.POModel;

public abstract class AbstractEventHandler implements EventHandler {

	protected IEventManager eventManager = CEventManager.getInstance();

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEventHandler.class);

	@Override
	public void handleEvent(CEvent event) {
		try {
			doHandleEvent(event);
		} catch (RuntimeException e) {
			addError(event, e);
			throw e;
		} catch (Exception e) {
			addError(event, e);
			throw new RuntimeException(e);
		} catch (Error e) {
			addError(event, e);
			throw e;
		} catch (Throwable e) {
			addError(event, e);
			throw new Error(e);
		}
	}
	
	/**
	 * override this method to handle event
	 * @param event
	 */
	protected abstract void doHandleEvent(CEvent event);
	
	/**
	 *
	 * @param eventTopic
	 */
	protected void registerEvent(String eventTopic) {
		this.registerEvent(eventTopic, null);
	}

	/**
	 *
	 * @param topic
	 * @param filter
	 */
	protected void registerEvent(String topic, String filter) {
		if (filter != null)
			eventManager.register(topic, filter, this);
		else
			eventManager.register(topic, this);
	}

	/**
	 * @param topic
	 * @param tableName
	 */
	protected void registerTableEvent(String topic, String tableName) {
		registerEvent(topic, tableName);
	}
	

	
	/**
	 * @param event
	 * @param e
	 */
	protected void addError(CEvent event, Throwable e) {
		String msg = e.getLocalizedMessage();
		if (msg == null) {
			msg = e.toString();			
		}
		
		addErrorMessage(event, msg);
	}
	
	/**
	 *
	 * @param <T>
	 * @param event
	 */
	protected <T> T getEventData(CEvent event) {
		return getEventProperty(event, IEventManager.EVENT_DATA);
	}
	
	/**
	 * @param event
	 * @return PO
	 */
	protected POModel getPO(CEvent event) {
		return getEventProperty(event, IEventManager.EVENT_DATA);
	}

	/**
	 *
	 * @param <T>
	 * @param event
	 * @param property
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getEventProperty(CEvent event, String property) {
		return (T) event.getProperty(property);
	}

	/**
	 * @param event
	 * @param errorMessage
	 */
	protected void addErrorMessage(CEvent event, String errorMessage) {
		List<String> errors = getEventProperty(event, IEventManager.EVENT_ERROR_MESSAGES);
		if (errors != null)
			errors.add(errorMessage);
	}
}
