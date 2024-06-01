package com.cadre.server.core.attachment;

import java.io.InputStream;

import com.cadre.server.core.annotation.CustomService;
import com.cadre.server.core.boundary.MediaStore;
import com.cadre.server.core.entity.MMediaFolder;

@CustomService(serviceId = MediaStore.class )
public class EmptyStoreImpl implements MediaStore {

	@Override
    public InputStream loadLOBData(MMediaFolder folder, String fileName) {
		return null;
	}

	@Override
	public boolean delete(MMediaFolder folder, String fileName){
		return true;
	}

	@Override
	public boolean uploadFileContent(MMediaFolder prov, String fileName, InputStream inputStream) {
		return true;
	}


}
