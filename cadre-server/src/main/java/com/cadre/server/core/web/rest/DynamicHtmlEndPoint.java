package com.cadre.server.core.web.rest;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.annotation.SecureAppEndPoint;
import com.cadre.server.core.entity.DatabaseOperation;
import com.cadre.server.core.entity.MNotificationTemplate;
import com.cadre.server.core.exception.CadreException;
import com.cadre.server.core.service.ModelService;
import com.cadre.server.core.util.FreeMarkerEngineUtils;


@Path("v1/html")
@Produces(MediaType.APPLICATION_JSON)
public class DynamicHtmlEndPoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicHtmlEndPoint.class);

	@Inject
	private ModelService modelService;
	
	@GET
	@Produces("text/html; charset=UTF-8")
	@SecureAppEndPoint(databaseOperation = DatabaseOperation.READ)
	public Response generateHTML(@QueryParam("resourceName") final String resourceName,@Context UriInfo info ) {
		try {
			if (null!=resourceName) {
				
				MNotificationTemplate template = modelService.getPO(null, MNotificationTemplate.TABLE_NAME, MNotificationTemplate.COLUMNNAME_Name,resourceName);
				
				Map<String,Object> model = info.getQueryParameters().entrySet().parallelStream()
						.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().findFirst().orElse(null)));
				
				String htmlFile = FreeMarkerEngineUtils.mergeTemplate(template, model);
				
				ResponseBuilder response = Response.ok(new ByteArrayInputStream(htmlFile.getBytes()));
				return response.build();
			}else {
				return CadreExceptionHandler.buildExceptionResponse(Status.BAD_REQUEST, "@InvalidParams@");
			}
		} catch (CadreException ex) {
			LOGGER.error("generateHTML(resourceName=,"+resourceName +")");

			return CadreExceptionHandler.buildExceptionResponse(ex);
		}
		
	}
	

}
