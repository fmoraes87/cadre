package com.cadre.server.core.boundary;

import java.io.InputStream;

import com.cadre.server.core.entity.MMediaFolder;


public interface MediaStore extends ServiceProvider {

	public static final String EMPTY_STORE = "empty";

    public InputStream loadLOBData(MMediaFolder folder, String fileName);

	boolean uploadFileContent(MMediaFolder folder, String fileName,InputStream inputStream);

	public boolean delete(MMediaFolder folder, String fileName);
}
