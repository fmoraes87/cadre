package com.cadre.server.core.resolver;

public enum ServiceType {

	NOTIFICATION_PROVIDER("2"),
	CUSTOM_SERVICE_IMPL("4"),
	EVENT_HANDLER("5"),
	IDENTITY_PROVIDER("1"),
	STORAGE("3");
	
	private String type;

	ServiceType(String type){
		this.type = type;	
	}
	
	
	
	public String getType() {
		return type;
	}



	public static ServiceType getAccessLevelByCode(String type) {
		for (ServiceType accessLevel : ServiceType.values()) {
			if (accessLevel.getType().equalsIgnoreCase(type)) {
				return accessLevel;				
			}
		}
		return null;
	}
	
	
}
