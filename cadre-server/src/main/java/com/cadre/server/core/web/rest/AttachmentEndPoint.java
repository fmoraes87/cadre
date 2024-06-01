package com.cadre.server.core.web.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.annotation.SecureUserEndPoint;
import com.cadre.server.core.broker.AttachmentService;
import com.cadre.server.core.entity.DatabaseOperation;
import com.cadre.server.core.entity.MMedia;
import com.cadre.server.core.entity.MMediaFolder;
import com.cadre.server.core.exception.CadreException;
import com.cadre.server.core.persistence.jdbc.Trx;
import com.cadre.server.core.resolver.DynamicServiceResolver;
import com.cadre.server.core.service.ModelService;


@Path("v1/attachment")
@Produces(MediaType.APPLICATION_JSON)
public class AttachmentEndPoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentEndPoint.class);

	@Inject
	private AttachmentService attachmentService;
	
	@Inject
	private ModelService modelService;
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@SecureUserEndPoint(databaseOperation = DatabaseOperation.WRITE)
	public Response upload(
			@FormDataParam("folderID") final String folderID,
			@FormDataParam("file") final InputStream inputStream,
			@FormDataParam("file") final FormDataContentDisposition fileInfo) {
		
		String fileName = fileInfo.getFileName();
		
		if (null!=folderID && null!=fileName) {
			final MMedia [] media=new MMedia[1];
			
			Trx trx = Trx.get(Trx.createTrxName(), true);

			try {
					
				MMediaFolder mediaFolder = modelService.getPO(trx.getTrxName(), MMediaFolder.TABLE_NAME,Integer.parseInt(folderID));
				media[0] = attachmentService.saveFile(trx.getTrxName(),fileName,mediaFolder,inputStream);
					
				return Response.status(Status.OK).entity(media[0]).build() ;					

			} catch (CadreException ex) {
				if (trx != null) {
					trx.rollback();
					trx.close();
					trx = null;
				}
				
				LOGGER.error("upload(folderID=,"+folderID +",fileName="+fileName+")");
				return CadreExceptionHandler.buildExceptionResponse(ex);
			}finally {
				if (trx != null) {
					trx.commit();
					trx.close();
				}
			}
			
		}else {
			return CadreExceptionHandler.buildExceptionResponse(Status.BAD_REQUEST, "@InvalidParams@");
		}
		
	}
	
	@GET
	@Path("/{mediaID}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@SecureUserEndPoint(databaseOperation = DatabaseOperation.READ)
	public Response download(@PathParam("mediaID") Integer mediaID) {
		
		if (null!=mediaID) {
			final MMedia media= attachmentService.loadFile(mediaID);
	
			  final StreamingOutput streamingOutput = new StreamingOutput() {
				    @Override
				    public void write(OutputStream output) throws IOException, WebApplicationException {
				      IOUtils.copy(media.getData(), output, 4096);
				    }
				  };
				  
			ResponseBuilder response =
					Response.ok(streamingOutput)
					.header("Content-Disposition", "attachment; filename=" + media.getValue());
			return response.build();

		}else {
			return CadreExceptionHandler.buildExceptionResponse(Status.BAD_REQUEST, "@InvalidParams@");
		}
		
	}
	
	/*@DELETE
	@Path("/{mediaID}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@SecureEndPoint(databaseOperation = DatabaseOperation.READ)
	public Response delete(@PathParam("mediaID") Integer mediaID) {
		
		if (null!=mediaID) {
			AttachmentService service =  DynamicServiceResolver.locate(AttachmentService.class);
			service.deleteMedia(mediaID);
	
			return Response.status(Status.OK).build() ;					


		}else {
			return CadreExceptionHandler.buildExceptionResponse(Status.BAD_REQUEST, "@InvalidParams@");
		}
		
	}*/

}
