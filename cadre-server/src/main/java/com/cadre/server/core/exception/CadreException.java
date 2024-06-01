package com.cadre.server.core.exception;

public class CadreException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String code;

	private int status;

	private String moreInfo;

	public CadreException(int status, String code) {
		super();
		this.status = status;
		this.code = code;
	}
	
	public CadreException(int status, String code, String moreInfo) {
		super();
		this.status = status;
		this.code = code;
		this.moreInfo = moreInfo;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMoreInfo() {
		return moreInfo;
	}

	public void setMoreInfo(String moreInfo) {
		this.moreInfo = moreInfo;
	}
	
	

}
