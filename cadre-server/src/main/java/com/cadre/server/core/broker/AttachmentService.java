package com.cadre.server.core.broker;

import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import com.cadre.server.core.boundary.MediaStore;
import com.cadre.server.core.entity.MMedia;
import com.cadre.server.core.entity.MMediaFolder;
import com.cadre.server.core.entity.MMediaFormat;
import com.cadre.server.core.entity.MServiceProvider;
import com.cadre.server.core.exception.CadreException;
import com.cadre.server.core.resolver.DynamicServiceResolver;
import com.cadre.server.core.resolver.ResolverQuery;
import com.cadre.server.core.resolver.ServiceType;
import com.cadre.server.core.service.ModelService;

@Singleton
public class AttachmentService {
	
	@Inject
	private ModelService modelService;
	
	public AttachmentService() {	}
	
	public MMedia saveFile(String trxName, String fileName, MMediaFolder folder, InputStream fileContent) {
		
		int pos = fileName.lastIndexOf(".");
		if (pos==-1) {
			throw new CadreException(Status.BAD_REQUEST.getStatusCode(),"@InvalidFormatFile@");
		}
		String extension = fileName.substring(pos);
		
		MMedia media = modelService.createPO(trxName, MMedia.TABLE_NAME);
		MMediaFormat mediaFormat = modelService.getPO(trxName, MMediaFormat.TABLE_NAME,MMediaFormat.COLUMNNAME_Extension,extension);

		media.setValue(fileName);
		media.setAD_MediaFormat_ID(mediaFormat.getAD_MediaFormat_ID());
		media.setAD_MediaFolder_ID(folder.getAD_MediaFolder_ID());
		modelService.save(media);
		
		ResolverQuery query = new ResolverQuery();
		query.put(MServiceProvider.COLUMNNAME_Value, StringUtils.defaultIfEmpty(folder.getMethod(), MediaStore.EMPTY_STORE));	
		query.put(MServiceProvider.COLUMNNAME_ServiceType, ServiceType.STORAGE);	
		
		MediaStore mediaStore = DynamicServiceResolver.locate(MediaStore.class, query);
		mediaStore.uploadFileContent(folder, fileName, fileContent);
		
		return media;
	}

	public MMedia loadFile(Integer mediaID) {
		if (null==mediaID || mediaID==0) {
			throw new IllegalArgumentException("mediaID==0");
		}
		
		MMedia media = modelService.getPO(null, MMedia.TABLE_NAME,mediaID);
		MMediaFolder folder = modelService.getPO(null, MMediaFolder.TABLE_NAME,media.getAD_MediaFolder_ID());
		
		ResolverQuery query = new ResolverQuery();
		query.put(MServiceProvider.COLUMNNAME_Value, StringUtils.defaultIfEmpty(folder.getMethod(), MediaStore.EMPTY_STORE));	
		query.put(MServiceProvider.COLUMNNAME_ServiceType, ServiceType.STORAGE);	

		MediaStore mediaStore = DynamicServiceResolver.locate(MediaStore.class, query);
		media.setData(mediaStore.loadLOBData(folder,media.getValue()));

		return media;
	}

	public void deleteMedia(Integer mediaID) {
		/*MMedia media = modelService.getPO(null, MMedia.TABLE_NAME,mediaID);
		MMediaFolder folder = modelService.getPO(null, MMediaFolder.TABLE_NAME,media.getAD_MediaFolder_ID());
		
		ResolverQuery query = new ResolverQuery();
		query.put(MMediaFolder.COLUMNNAME_Method, StringUtils.defaultIfEmpty(folder.getMethod(), MediaStore.EMPTY_STORE));	
		
		MediaStore mediaStore = DynamicServiceResolver.locate(MediaStore.class, query);
		mediaStore.delete(folder, media.getValue());*/
		
	}

}
