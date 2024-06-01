package com.cadre.server.core.entity;

public enum AccessLevel {
	ACCESSLEVEL_Organization("1"),
	ACCESSLEVEL_ClientPlusOrganization("3"),
	ACCESSLEVEL_SystemOnly("4"),
	ACCESSLEVEL_All("7"),
	ACCESSLEVEL_SystemPlusClient("6"),
	ACCESSLEVEL_ClientOnly("2");
	

	private String code;

	AccessLevel(String code){
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
	public static AccessLevel getAccessLevelByCode(String code) {
		for (AccessLevel accessLevel : AccessLevel.values()) {
			if (accessLevel.getCode().equalsIgnoreCase(code))
				return accessLevel;
		}
		return null;
	}
	
	
}
