package com.cadre.server.core.security.oauth2;

import com.cadre.server.core.entity.MessageType;

public enum OAuthMessageType {
	
	ERROR_ACCESSTOKEN_INVALID("001", MessageType.ERROR, "401"),
	ERROR_TOKEN_WITHOUT_DEFAULT_USER("002", MessageType.ERROR, "401"),
	ERROR_REFRESHTOKEN_INVALID("003", MessageType.ERROR, "401"),
	ERROR_OAUTHCLIENT_INVALID_CREDENTIALS("004", MessageType.ERROR, "401"),
	ERROR_OAUTHCLIENT_GRANTTYPE_NOTALLOWED("005", MessageType.ERROR, "401"),
	ERROR_OAUTHCLIENT_INVALID("006", MessageType.ERROR, "401"),
	ERROR_CREATE_TOKEN_NOTALLOWED("007", MessageType.ERROR, "401"),
	SUCCESS_TOKEN_CREATED_SUCCESSFULLY("008", MessageType.SUCCESS, "200"),
	ERROR_AUTHORIZATIONCODE_INVALID("009", MessageType.ERROR, "401"),
	ERROR_REDIRECTURI_INVALID("010", MessageType.ERROR, "401"),
	ERROR_REDIRECTURI_UNSECURE("011", MessageType.ERROR, "401"),
	ERROR_AUTHORIZATION_USER_INVALID("012", MessageType.ERROR, "401"),
	ERROR_RESPONSE_TYPE_INVALID("013", MessageType.ERROR, "401"),
	ERROR_GRANT_TYPE_INVALID("014", MessageType.ERROR, "401"),
	SUCCESS_CREDENTIALS_CREATED_SUCCESSFULLY("015", MessageType.SUCCESS, "200");

	/**
	 * Message code
	 */
	private String code;

	private MessageType type;

	private String status;

	/**
	 * 
	 * Create a new instance of NotificationMessageType
	 * 
	 * @param pCode
	 */
	private OAuthMessageType(String pCode, MessageType pType, String pStatus) {
		code = pCode;
		type = pType;
		status = pStatus;
	}


    public String getCode() {
        return code;
    }


    public MessageType getType() {
        return type;
    }


    public String getStatus() {
        return status;
    }
}
