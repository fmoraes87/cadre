package com.cadre.server.core.util;

import java.util.Base64;

import org.apache.commons.codec.digest.DigestUtils;

import com.cadre.server.core.broker.SysConfigServiceBroker;
import com.cadre.server.core.entity.MUser;

public class BaseUtil {

	public static final String HTTP_BASE_HOST = "HTTP_BASE_HOST";
	private static final String UPDATE_USER_SERVICE = "/update-user?transactionCode=";

	
	public static String getURLToUpdatePassword(String key, MUser user) {
		String baseUrl = SysConfigServiceBroker.getValue(HTTP_BASE_HOST);
		int type = 1;
		
		StringBuffer url = new StringBuffer(baseUrl);
		url.append(UPDATE_USER_SERVICE);//service
		
		String trxCode = JSONUtils.createJsonWith(user,MUser.COLUMNNAME_AD_User_UU, MUser.COLUMNNAME_UserPIN).toString();
		String finalJson = String.format("{\"type\":%s,\"trxCode\":\"%s\"}",type,Base64.getEncoder().encodeToString(SecurityUtils.encripty(key,trxCode)));
		
		StringBuffer transactionCode = new StringBuffer();
		transactionCode.append(finalJson);
		transactionCode.append("@");
		transactionCode.append(DigestUtils.md5Hex(finalJson));

		url.append(Base64.getEncoder().encodeToString(transactionCode.toString().getBytes()));//service
		
		
		return url.toString();
	}
	
	public static String getURLToConfirmAccount(String key,MUser user) {
		String baseUrl = SysConfigServiceBroker.getValue(HTTP_BASE_HOST);

		int type = 2;
		
		StringBuffer url = new StringBuffer(baseUrl);
		url.append(UPDATE_USER_SERVICE);//service
		
		String trxCode = JSONUtils.createJsonWith(user,MUser.COLUMNNAME_AD_User_UU).toString();
		String finalJson = String.format("{\"type\":%s,\"trxCode\":\"%s\"}",type,Base64.getEncoder().encodeToString(SecurityUtils.encripty(key,trxCode)));
		
		StringBuffer transactionCode = new StringBuffer();
		transactionCode.append(finalJson);
		transactionCode.append("@");
		transactionCode.append(DigestUtils.md5Hex(finalJson));

		url.append(Base64.getEncoder().encodeToString(transactionCode.toString().getBytes()));//service
		
		
		return url.toString();
	}

	
}
