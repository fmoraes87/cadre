package com.cadre.server.core.persistence.query;

import org.apache.commons.lang3.StringUtils;

public class GenericSearchOrderBy {

	private static final String DESC_MODE = StringUtils.SPACE+"desc";
	private static final String ASC_MODE = StringUtils.SPACE+"asc";
	private String columnName;
	private boolean asc;
	
	public GenericSearchOrderBy(String columnName) {
		this(columnName,true);
	}

	public GenericSearchOrderBy(String columnName, boolean asc) {
		this.columnName = columnName;
		this.asc = asc;
	}
	
	
	public String toString() {
		return this.columnName 
				+ (asc ? ASC_MODE : DESC_MODE);
	}
}
