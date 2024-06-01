package com.cadre.server.core.broker;

import com.cadre.server.core.entity.MUser;
import com.cadre.server.core.security.IdentityBrokerException;
import com.cadre.server.core.web.IdentityRequest;
import com.cadre.server.core.web.IdentityResponse;

public interface IdentityService {

	IdentityResponse login(IdentityRequest loginRequest, String trxName) throws IdentityBrokerException;

	MUser createUser(IdentityRequest request, String trxName);

	MUser confirmUserAccount(String trxName, String trxCode);

	/**
	 * From email
	 * @param trxName
	 * @param trxCode
	 * @param newUserPin
	 */
	void updateUserPassword(String trxName, String trxCode, String newUserPin);
	
	/**
	 * From AppClient
	 * @param trxName
	 * @param trxCode
	 * @param newUserPin
	 */
	void updateMyPassword(String trxName, String trxCode, String newUserPin);


	void resetPassword(String email);
	
	void sendConfirmUserPassword(MUser user);


}