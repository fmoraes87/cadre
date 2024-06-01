package com.cadre.server.core.entity;

import java.io.Serializable;

import com.cadre.server.core.util.DisplayType;

public class POInfoColumn implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public int adColumnID;
	public String columnName;
	public int adReferenceID;
	public boolean isMandatory;
	public String name;
	public String description;
	public boolean isKey;
	public boolean isTranslatable;
	public boolean isUpdatable;
	public boolean isIdentifier;

	public Class<?> columnClass;

	public POInfoColumn(int adColumnID, String columnName, int adReferenceID, boolean isMandatory,
			String name, String description, boolean isKey, boolean isTranslatable, boolean isUpdatable, boolean isIdentifier) {
		this.adColumnID = adColumnID;
		this.columnName = columnName;
		this.adReferenceID = adReferenceID;
		this.isMandatory = isMandatory;
		this.name = name;
		this.description = description;
		this.isKey = isKey;
		this.columnClass = DisplayType.getClass(adReferenceID, true);
		this.isTranslatable=isTranslatable;
		this.isUpdatable=isUpdatable;
		this.isIdentifier=isIdentifier;

	}
	
	public String getColumnName() {
		return columnName;
	}

	public boolean isNaturalKey() {
		return isIdentifier
				|| this.columnName.toLowerCase().equals("value");
	}

	/**
	 * 	String representation
	 *  @return info
	 */
	public String toString()
	{
		StringBuilder sb = new StringBuilder("POInfo.Column[");
		sb.append(columnName).append(",ID=").append(adColumnID)
			.append(",DisplayType=").append(adReferenceID)
			.append(",ColumnClass=").append(columnClass);
		sb.append("]");
		return sb.toString();
	}	//	toString

}
