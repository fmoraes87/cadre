package com.cadre.server.core.web.rest;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.annotation.SecureAppEndPoint;
import com.cadre.server.core.broker.IdentityService;
import com.cadre.server.core.exception.CadreException;
import com.cadre.server.core.persistence.jdbc.Trx;
import com.cadre.server.core.util.MessageUtils;
import com.cadre.server.core.web.IdentityRequest;
import com.cadre.server.core.web.IdentityResponse;

@Path("v1/token")
@Produces(MediaType.APPLICATION_JSON)
public class TokenEndPoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(TokenEndPoint.class);
	
	@Inject
	private IdentityService identityBroker;

	@POST
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@SecureAppEndPoint
	public Response getToken(@Context HttpServletRequest request, MultivaluedMap<String, String> form) {

		IdentityResponse[] response = new IdentityResponse[1];

		Trx trx = Trx.get(Trx.createTrxName(), true);
		try {
			IdentityRequest loginRequest = new IdentityRequest(request, form);
			response[0]= identityBroker.login(loginRequest, trx.getTrxName());
		} catch (CadreException ex) {
			if (trx != null) {
				trx.rollback();
				trx.close();
				trx = null;
			}
			
			LOGGER.error("getToken");
			return CadreExceptionHandler.buildExceptionResponse(ex);
		}finally {
			if (trx != null) {
				trx.commit();
				trx.close();
			}
		}


		return Response.status(response[0].getStatus())
				.entity(MessageUtils.parseMessage((String) response[0].getBody())).build();
		
		
	}


}
