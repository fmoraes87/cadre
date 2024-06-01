package com.cadre.server.core.web.rest;

import javax.inject.Inject;
import javax.json.bind.JsonbException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.annotation.SecureAppEndPoint;
import com.cadre.server.core.broker.IdentityService;
import com.cadre.server.core.entity.MUser;
import com.cadre.server.core.exception.CadreException;
import com.cadre.server.core.persistence.jdbc.Trx;
import com.cadre.server.core.util.MessageUtils;
import com.cadre.server.core.util.SecurityUtils;
import com.cadre.server.core.web.IdentityRequest;

@Path("v1/user")
public class UserEndPoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserEndPoint.class);

	private static final String BODY_TRANSACTION_CODE = "trxCode";
	
	@Inject
	private IdentityService identityBroker;

	@POST
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@SecureAppEndPoint
	public Response createUser(@Context HttpServletRequest request, MultivaluedMap<String, String> form) {

		OAuthResponse[] oAuthResponse = new OAuthResponse[1];

		Trx trx = Trx.get(Trx.createTrxName(), true);

		try {
			IdentityRequest createUserRequest = new IdentityRequest(request, form);
			MUser user = identityBroker.createUser(createUserRequest, trx.getTrxName());

			trx.setSavepoint("saveUser");

			try {
								
				SecurityUtils.validateUser(trx.getTrxName(),user);
				
				oAuthResponse[0] = SecurityUtils.generateOAuthResponse(trx.getTrxName(),user);					
				
			} catch (OAuthSystemException ex) {
				trx.rollback();
				LOGGER.error("createUser(form=" +form + ")=> FORBIDDEN", ex);
				throw new CadreException(Status.FORBIDDEN.getStatusCode(), ex.getMessage());

			} catch (JsonbException ex) {
				trx.rollback();
				LOGGER.error("createUser(form=" +form + ")=> BAD_REQUEST", ex);
				throw new CadreException(Status.BAD_REQUEST.getStatusCode(), ex.getMessage());
			}

		} catch (CadreException ex) {
			if (trx != null) {
				trx.rollback();
				trx.close();
				trx = null;
			}
			LOGGER.error("createUser(form=" +form + ")", ex);
			return CadreExceptionHandler.buildExceptionResponse(ex);
		}finally {
			if (trx != null) {
				trx.commit();
				trx.close();
			}
		}

		return Response.status(oAuthResponse[0].getResponseStatus())
				.entity(MessageUtils.parseMessage(oAuthResponse[0].getBody())).build();

	}
	
	@GET
	@Path("/confirm")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@SecureAppEndPoint
	public Response confirm(@QueryParam(BODY_TRANSACTION_CODE) final String trxCode) {
		OAuthResponse[] oAuthResponse = new OAuthResponse[1];

		Trx trx = Trx.get(Trx.createTrxName(), true);
		try {
		
				try {
					
					MUser user = identityBroker.confirmUserAccount(trx.getTrxName(),trxCode);

					oAuthResponse[0] = SecurityUtils.generateOAuthResponse(trx.getTrxName(),user);					
					
				} catch (OAuthSystemException ex) {
					LOGGER.error("confirm(trxCode=" +trxCode + ")=> FORBIDDEN", ex);

					throw new CadreException(Status.FORBIDDEN.getStatusCode(), ex.getMessage());

				} catch (JsonbException ex) {
					LOGGER.error("confirm(trxCode=" +trxCode + ")=> BAD_REQUEST", ex);

					throw new CadreException(Status.BAD_REQUEST.getStatusCode(), ex.getMessage());

				}

		} catch (CadreException ex) {
			if (trx != null) {
				trx.rollback();
				trx.close();
				trx = null;
			}
			LOGGER.error("confirm(trxCode=" +trxCode + ")", ex);
			return CadreExceptionHandler.buildExceptionResponse(ex);
		}finally {
			if (trx != null) {
				trx.commit();
				trx.close();
			}		
		}

		return Response.status(oAuthResponse[0].getResponseStatus())
				.entity(MessageUtils.parseMessage(oAuthResponse[0].getBody())).build();

	}
	
	@POST
	@Path("/resetPassword")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@SecureAppEndPoint
	public Response resetPassword(@Context HttpServletRequest request, MultivaluedMap<String, String> form) {

		String email = form.getFirst(MUser.COLUMNNAME_EMailUser);
		if (StringUtils.isNotEmpty(email)) {
			identityBroker.resetPassword(email.toLowerCase());
		}

		return Response.noContent().build();			

	}
	
	@PUT
	@Path("/updateUserPassword")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@SecureAppEndPoint
	public Response updateUserPassword(@Context HttpServletRequest request, MultivaluedMap<String, String> form) {

		String trxCode = form.getFirst(BODY_TRANSACTION_CODE);
		String password = form.getFirst(MUser.COLUMNNAME_UserPIN);
		
		Trx trx = Trx.get(Trx.createTrxName(), true);
		try {
			identityBroker.updateUserPassword(trx.getTrxName(),trxCode,password);

		} catch (CadreException ex) {
			if (trx != null) {
				trx.rollback();
				trx.close();
				trx = null;
			}
			
			LOGGER.error("updateUserPassword(trxCode=" +trxCode + ")", ex);
			return CadreExceptionHandler.buildExceptionResponse(ex);
		}finally {
			if (trx != null) {
				trx.commit();
				trx.close();
			}		
		}
		
		

		return Response.noContent().build();

	}
	
	@PUT
	@Path("/updateMyPassword")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@SecureAppEndPoint
	public Response updateMyPassword(@Context HttpServletRequest request, MultivaluedMap<String, String> form) {

		String trxCode = form.getFirst(BODY_TRANSACTION_CODE);
		String password = form.getFirst(MUser.COLUMNNAME_UserPIN);
		
		Trx trx = Trx.get(Trx.createTrxName(), true);
		try {
			identityBroker.updateMyPassword(trx.getTrxName(),trxCode,password);

		} catch (CadreException ex) {
			if (trx != null) {
				trx.rollback();
				trx.close();
				trx = null;
			}
			
			LOGGER.error("updateUserPassword(trxCode=" +trxCode + ")", ex);
			return CadreExceptionHandler.buildExceptionResponse(ex);
		}finally {
			if (trx != null) {
				trx.commit();
				trx.close();
			}
		}

		return Response.noContent().build();

	}

}
