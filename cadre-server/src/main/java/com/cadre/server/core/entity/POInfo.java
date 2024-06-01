package com.cadre.server.core.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.util.DisplayType;

public class POInfo {

	/** Table_ID */
	private int tableID = 0;
	/** Table Name */
	private String tableName = null;

	/** Columns */
	private POInfoColumn[] columns = null;
	
	/** column name to index map **/
	private Map<String, Integer> columnNameMap;
	/** ad_column_id to index map **/
	private Map<Integer, Integer> columnIdMap;
	private boolean translated;
	private AccessLevel accessLevel;

	public POInfo(int tableID, boolean translated, String tableName,  AccessLevel accessLevel, List<POInfoColumn> list) {
		this.tableID = tableID;
		this.translated = translated;
		this.tableName = tableName;
		this.accessLevel = accessLevel;

		// conver to array
		columns = new POInfoColumn[list.size()];
		list.toArray(columns);
		
		columnNameMap = new HashMap<String, Integer>();
		columnIdMap = new HashMap<Integer, Integer>();
		
		Iterator<POInfoColumn> it = list.iterator();
		int index = 0;
		while (it.hasNext()) {
			POInfoColumn poInfo = it.next();
			columnNameMap.put(poInfo.columnName.toUpperCase(), index);
			columnIdMap.put(poInfo.adColumnID, index);
			
			index++;
		}

		
	}

	public int getTableID() {
		return tableID;
	}

	public void setTableID(int tableID) {
		this.tableID = tableID;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public POInfoColumn[] getColumns() {
		return columns;
	}
	
	public boolean isTranslated() {
		return translated;
	}

	public void setTranslated(boolean translated) {
		this.translated = translated;
	}

	public AccessLevel getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(AccessLevel accessLevel) {
		this.accessLevel = accessLevel;
	}

	/**
	 * Get columns Name
	 * @param translate
	 * @return
	 */
	public List<String> getColumnsName() {
		return Arrays.asList(columns).stream()
				.map(c -> {
					StringBuilder sql = new StringBuilder();
					sql.append("COALESCE (");
					sql.append(tableName);
					if (isTranslated() 
							&& !CadreEnv.isBaseLanguageSelected() 
							&& c.isTranslatable) {
						sql.append(MTable.TRL_PREFIX); 
					}
					sql.append(".");
					sql.append(c.columnName);
					sql.append(", ");
					sql.append(tableName);
					sql.append(".");
					sql.append(c.columnName);
					sql.append(")");
					sql.append(" as ");
					sql.append(c.columnName);
					
					return sql.toString();
				})
				.collect(Collectors.toList());
	}


	public void setColumns(POInfoColumn[] columns) {
		this.columns = columns;
	}

	/**
	 * String representation
	 * 
	 * @return String Representation
	 */
	public String toString() {
		return "POInfo[" + getTableName() + ",AD_Table_ID=" + getTableID() + "]";
	} // toString

	/**************************************************************************
	 * Get ColumnCount
	 * 
	 * @return column count
	 */
	public int getColumnCount() {
		return columns.length;
	}

	/**
	 * Get Column Index
	 * 
	 * @param ColumnName column name
	 * @return index of column with ColumnName or -1 if not found
	 */
	public int getColumnIndex(String columnName) {
		Integer i = columnNameMap.get(columnName.toUpperCase());
		if (i != null)
			return i.intValue();

		return -1;
	} // getColumnIndex

	/**
	 * Get Column Index
	 * 
	 * @param AD_Column_ID column
	 * @return index of column with ColumnName or -1 if not found
	 */
	public int getColumnIndex(int adColumnID) {
		Integer i = columnIdMap.get(adColumnID);
		if (i != null)
			return i.intValue();

		return -1;
	} // getColumnIndex

	/**
	 * @param columnName
	 * @return AD_Column_ID if found, -1 if not found
	 */
	public int getAD_Column_ID(String columnName) {
		for (int i = 0; i < columns.length; i++) {
			if (columnName.equalsIgnoreCase(columns[i].columnName)) {
				return columns[i].adColumnID;				
			}
																	
		}
		return -1;
	}

	/**
	 * Get Column
	 * 
	 * @param index index
	 * @return column
	 */
	protected POInfoColumn getColumn(int index) {
		if (index < 0 || index >= columns.length)
			return null;
		return columns[index];
	} // getColumn

	/**
	 * Get Column Name
	 * 
	 * @param index index
	 * @return ColumnName column name
	 */
	public String getColumnName(int index) {
		if (index < 0 || index >= columns.length)
			return null;
		return columns[index].columnName;
	} // getColumnName
	

	/**
	 *  Is Column Key
	 *  @param index index
	 *  @return true if column is the key
	 */
	public boolean isKey (int index)
	{
		if (index < 0 || index >= columns.length)
			return false;
		return columns[index].isKey;
	}   //  isKey
	

	/**
	 *  Is Column Mandatory
	 *  @param index index
	 *  @return true if column mandatory
	 */
	public boolean isColumnMandatory (int index)
	{
		if (index < 0 || index >= columns.length)
			return false;
		return columns[index].isMandatory;
	}   //  isMandatory

	/**
	 *  Get Column Class
	 *  @param index index
	 *  @return Class
	 */
	public Class<?> getColumnClass (int index)
	{
		if (index < 0 || index >= columns.length)
			return null;
		return columns[index].columnClass;
	}   //  getColumnClass


	/**
	 *  Get Column Display Type
	 *  @param index index
	 *  @return DisplayType
	 */
	public int getColumnDisplayType (int index)
	{
		if (index < 0 || index >= columns.length)
			return DisplayType.String;
		
		return columns[index].adReferenceID;
	}   //  getColumnDisplayType


	public POInfoColumn getPOInfoColumn(String parameterName) {
		return Arrays.asList(columns)
				.stream()
				.filter(column -> column.getColumnName().equals(parameterName))
				.findFirst().orElse(null);
	}


}
