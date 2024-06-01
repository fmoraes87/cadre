package com.cadre.server.core.web.rest;

import java.util.Optional;

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
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.annotation.SecureUserEndPoint;
import com.cadre.server.core.entity.DatabaseOperation;
import com.cadre.server.core.exception.CadreException;
import com.cadre.server.core.persistence.jdbc.Trx;
import com.cadre.server.core.process.CadreProcess;
import com.cadre.server.core.process.SvrProcessEngine;
import com.cadre.server.core.process.WebSvrProcessRequest;
import com.cadre.server.core.web.RequestWrapper;

@Path("v1")
@Produces(MediaType.APPLICATION_JSON)
public class SvrProcessEndPoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(SvrProcessEndPoint.class);

	/**
	 * @since 1.0
	 */
	public static final String URL = "process";
	
	@Inject
	private SvrProcessEngine engine;

	@POST
	@Path(URL)
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@SecureUserEndPoint(databaseOperation = DatabaseOperation.WRITE)
	public Response process(@Context HttpServletRequest request, MultivaluedMap<String, String> form) {

		Trx trx = Trx.get(Trx.createTrxName(), true);

		try {
			WebSvrProcessRequest svrProcessRequest = new WebSvrProcessRequest(new RequestWrapper(request,form));
			CadreProcess proc = engine.getSvrProcess(svrProcessRequest.getProcessName());
			proc.setParams(svrProcessRequest.getParams());
			
			Optional<Object> result = proc.execute(trx.getTrxName());
			if (result.isPresent()) {
				return Response.status(Status.OK).entity(result).build();				
			}else {
				return Response.status(Status.OK).build();				
			}
			
		} catch (CadreException ex) {
			trx.rollback();
			trx.close();
			trx = null;
			
			LOGGER.error("process");

   		   return CadreExceptionHandler.buildExceptionResponse(ex);

		}finally {
			if (trx != null) {
				trx.commit();
				trx.close();
			}
		}


	}

}
