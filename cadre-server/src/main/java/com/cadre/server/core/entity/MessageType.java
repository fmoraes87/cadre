package com.cadre.server.core.entity;

public enum MessageType {
	WARNING("W", "Warning"), 
	INFORMATION("I", "Information"), 
	SUCCESS("S", "Success"), 
	ERROR("E", "ERROR");

	private String value;

	private String description;

	MessageType(String value, String description) {
		this.value = value;
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return value;
	}

	public static MessageType getMessagesTypeByValue(String type) {
		for (MessageType messageType : MessageType.values()) {
			if (messageType.getValue().equalsIgnoreCase(type))
				return messageType;
		}
		return null;
	}
}
