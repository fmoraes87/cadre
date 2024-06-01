package com.cadre.server.core.security;

import javax.ws.rs.core.Response.Status;

import org.apache.oltu.oauth2.common.message.OAuthResponse;

import com.cadre.server.core.boundary.ServiceProvider;
import com.cadre.server.core.entity.MUser;
import com.cadre.server.core.web.IdentityRequest;
import com.cadre.server.core.web.IdentityResponse;

public interface IdentityProvider extends ServiceProvider {
	
	static final String ACCESS_TOKEN = "accessToken";

	
	default IdentityResponse convetToIdentityResponse(final OAuthResponse oAuthResponse) {
		return new IdentityResponse() {
			
			@Override
			public Status getStatus() {
				return Status.fromStatusCode(oAuthResponse.getResponseStatus());
			}
			
			@Override
			public Object getBody() {
				return oAuthResponse.getBody();
			}

			@Override
			public boolean isSuccess() {
				return oAuthResponse.getResponseStatus()==Status.OK.getStatusCode();
			}
		};
	}

	public IdentityResponse login(IdentityRequest loginRequest, String trxName) throws IdentityBrokerException;
	public void logout(String trxName)  throws IdentityBrokerException;
	public MUser createUser(IdentityRequest createUserRequest, String trxName) throws IdentityBrokerException;
}
