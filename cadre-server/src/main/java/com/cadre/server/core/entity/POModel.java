package com.cadre.server.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.exception.CadreException;

/**
 * 
 * @author fernando
 *
 */
public abstract class POModel implements Serializable, Cloneable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(POModel.class);


	public static final String NO_VALUE = "N";
	public static final String YES_VALUE = "Y";
	
	private static final String MSG_COLUMN_NOT_FOUND = "Column not found";
	/** Column name AD_Client_ID */
	public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";
	/** Column name AD_Org_ID */
	public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";
	/** Column name Created */
	public static final String COLUMNNAME_Created = "Created";
	/** Column name CreatedBy */
	public static final String COLUMNNAME_CreatedBy = "CreatedBy";
	/** Column name IsActive */
	public static final String COLUMNNAME_IsActive = "IsActive";
	/** Column name Updated */
	public static final String COLUMNNAME_Updated = "Updated";
	/** Column name UpdatedBy */
	public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";	
	/** Column name UpdatedBy */
	public static final String COLUMNNAME_LANGUAGE = "AD_Language";	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int ID_ZERO = 0;
	
	
	/** Original Values */
	private Object[] m_oldValues = null;

	/** New Values */
	private Object[] m_newValues = null;
	
	/**	Get Actions */
	private Map<String,Callable<?>> getterMethods = new HashMap<>();
	
	/**	Set Actions */
	private Map<String,Consumer> setterMethods = new HashMap<>();

	/** column name to index map **/
	private POInfo poInfo = null;

	/**	Optional Transaction		*/
	private String			m_trxName = null;

	/**
	 * 	Set Trx
	 *	@param trxName transaction
	 */
	public void set_TrxName (String trxName)
	{
		m_trxName = trxName;
	}	//	setTrx

	/**
	 * 	Get Trx
	 *	@return transaction
	 */
	public String get_TrxName()
	{
		return m_trxName;
	}	//	getTrx
	
	
	/**
	 * 
	 * @return
	 */
	public POInfo getPOInfo() {
		return poInfo;
	}

	/**
	 * 
	 * @param poInfo
	 */
	public void setPoInfo(POInfo poInfo) {
		if (poInfo!=null) {
			this.poInfo = poInfo;
			m_oldValues = new Object[get_ColumnCount()];
			m_newValues = new Object[get_ColumnCount()];			
		}
	
	}
	
	
	public void configCurrentValues(Object[] m_oldValues) {
		this.m_oldValues = m_oldValues;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		POModel clone = (POModel) super.clone();
		if (m_newValues != null)
		{
			clone.m_newValues = new Object[m_newValues.length];
			for(int i = 0; i < m_newValues.length; i++)
			{
				clone.m_newValues[i] = m_newValues[i];
			}
		}
		if (m_oldValues != null)
		{
			clone.m_oldValues = new Object[m_oldValues.length];
			for(int i = 0; i < m_oldValues.length; i++)
			{
				clone.m_oldValues[i] = m_oldValues[i];
			}
		}
		
		clone.m_oldValues[this.get_ColumnIndex(this.getKeyColumnName())]=null;
		clone.m_newValues[this.get_ColumnIndex(this.getKeyColumnName())]=null;

		return clone;
		
	}

	public Object[] getCurrentValues() {
		return m_oldValues;
	}

	/**
	 *  Get TableName.
	 *  @return table name
	 */
	public String get_TableName()
	{
		return poInfo.getTableName();
	}  

	/**
	 *  Get Table ID.
	 *  @return table id
	 */
	public int get_Table_ID()
	{
		return poInfo.getTableID();
	}   //  get_TableID


	/**************************************************************************
	 *  Get Column Count
	 *  @return column count
	 */
	public int get_ColumnCount()
	{
		return poInfo.getColumnCount();
	}   //  getColumnCount

	/**
	 * 
	 * @param index
	 * @return
	 */
	private Object getValueOfColumn(int index) {

		if (this.poInfo==null || index < 0 || index >= this.poInfo.getColumnCount()) 
		{
			return null;
		}
		if (m_newValues[index] != null)
		{
			if (m_newValues[index].equals(Null.NULL)) {
				return null;
			}
			
			return m_newValues[index];
		}
		return m_oldValues[index];

	}

	/**
	 * 
	 * @param columnName
	 * @return
	 */
	public Object getValueOfColumn(String columnName) {
      int index = this.get_ColumnIndex(columnName);
      if (index < 0 ) {
        return null;
      }
      
      return actionGetMethodOrDefault(index,getterMethods.get(columnName)); 
    		  		
      
    }
	
	/**
	 * 
	 * @param columnName
	 * @return
	 */
	protected Object getValueNoCheck(String columnName) {
		int index = this.get_ColumnIndex(columnName);
		if (index < 0) {
			return null;
		}

		return getValueOfColumn(index);

	}
	
	/**
	 * 
	 * @param columnName
	 * @return
	 */
	protected void setValueNoCheck(String columnName,Object value) {
	      int index = this.get_ColumnIndex(columnName);
	      if (index >= 0 ) {
	    	  this.setValueOfColumn(index, value);
	      }else {
	    	  throw new IllegalArgumentException(MSG_COLUMN_NOT_FOUND);
	      }
	}


	private Object actionGetMethodOrDefault(int index,Callable<?> methodGet) {
		if (methodGet!=null) {
			try {
				return methodGet.call();
			} catch (Exception e) {
				LOGGER.error("actionGetMethodOrDefault("+index+","+ methodGet.toString()+ ")", e);
				return getValueOfColumn(index);
			}
		}else {
			return getValueOfColumn(index);
		}
	}

	/**
	 * 
	 * @param columnName
	 * @return
	 */
	public int get_ColumnIndex(String columnName) {
		return poInfo.getColumnIndex(columnName);
	}

	/**
	 * 
	 * @param columnName
	 * @param value
	 */
	public void setValueOfColumn(String columnName, Object value){
      int index = this.get_ColumnIndex(columnName);
      if (index >= 0 ) {
    	  actionSetMethodOrDefault(index,setterMethods.get(columnName), value); 
      }else {
    	  throw new IllegalArgumentException(MSG_COLUMN_NOT_FOUND);
      }

    }
	
	private void actionSetMethodOrDefault(int index, Consumer<Object> methodSet, Object value) {
		if (methodSet != null) {
			methodSet.accept(value);

		} else {
			this.setValueOfColumn(index, value);
		}

	}

	public void loadInicialValueFromDB(int index, Object value) {
		if (index < 0 || index >= get_ColumnCount())
		{
			return;
		}
		
		if (value == null)
		{
			if (poInfo.isColumnMandatory(index))
			{
				return;
			}
			m_oldValues[index] = null;          //  correct
		}else {
			setValue(m_oldValues,index, value);			
		}
		
		
	}


	/**
	 * 
	 * @param index
	 * @param value
	 */
	private void setValueOfColumn(int index, Object value){

		POInfoColumn infoColum = poInfo.getColumn(index);
			
		if (index < 0 || index >= get_ColumnCount())
		{
			return;
		}			
		//
		if (value == null)
		{
			if (poInfo.isColumnMandatory(index))
			{
				throw new CadreException(Status.BAD_REQUEST.getStatusCode(),"@InvalidValue@ : " + infoColum.columnName);
			}
			m_newValues[index] = Null.NULL;          //  correct
		}
		else
		{
			setValue(m_newValues,index, value);
		}
			
		//set_Keys (ColumnName, m_newValues[index]);

    }

	private void setValue(Object[] columnValues, int index, Object value) {
		//  matching class or generic object
		if (value.getClass().equals(poInfo.getColumnClass(index))
			|| poInfo.getColumnClass(index) == Object.class)
			columnValues[index] = value;     //  correct
		//  Integer can be set as BigDecimal
		else if (value.getClass() == BigDecimal.class
			&& poInfo.getColumnClass(index) == Integer.class)
			columnValues[index] = Integer.valueOf(((BigDecimal)value).intValue());
		//	Set Boolean
		else if (poInfo.getColumnClass(index) == Boolean.class
			&& (YES_VALUE.equals(value) || NO_VALUE.equals(value)) )
			columnValues[index] = Boolean.valueOf(YES_VALUE.equals(value));

		else if (value.getClass() == Integer.class
				&& poInfo.getColumnClass(index) == String.class)
			columnValues[index] = value;
		else if (value.getClass() == String.class
				&& poInfo.getColumnClass(index) == Integer.class) {
			try
			{
				columnValues[index] = Integer.valueOf((String)value);
			}
			catch (NumberFormatException e)
			{
				String errmsg = 
						"setValue - Class invalid: " + value.getClass().toString()
						+ ", Should be " + poInfo.getColumnClass(index).toString() + ": " + value;
				
				LOGGER.error(errmsg,e);
				return; 
			}
		}else
		{
			String errmsg = 
					"setValue - Class invalid: " + value.getClass().toString()
					+ ", Should be " + poInfo.getColumnClass(index).toString() + ": " + value;
			
			LOGGER.error(errmsg);

			return;
		}
	}
	
	//TODO - user IsKey property to identify key column
    public String getKeyColumnName(){
      String tableName = this.get_TableName().toLowerCase();
      String keyColumnName = tableName + "_id";

      return keyColumnName;
    }

    public Integer getKeyValue(){
      String keyColumnName = this.getKeyColumnName();
      Object keyColumnValue = this.getValueOfColumn(keyColumnName);
      
      return (Integer) keyColumnValue;
    }

    public void setKeyValue(Integer value){
      this.setValueOfColumn(this.getKeyColumnName(),value);
    }

    public boolean isNew(){
        Object tableColumnIdValue = this.getKeyValue();
        return tableColumnIdValue==null;
      }

     public boolean isChanged(){
 		int size = get_ColumnCount();
 		for (int i = 0; i < size; i++)
 		{
 			if (isValueChanged(i))
 				return true;
 		}

 		return false;
      }
     
     public boolean isValueChanged(String columnName) {
 		return this.isValueChanged(get_ColumnIndex(columnName));
     }
     

      public boolean isValueChanged(int index) {
  		if (index < 0 || index >= get_ColumnCount())
  		{
  			return false;
  		}
  		if (m_newValues[index] == null)
  			return false;
  		if (m_newValues[index] == null && m_oldValues[index] == null)
  			return false;
  		return !m_newValues[index].equals(m_oldValues[index]);
      }

  	/**
  	 * 	Is Standard Column
  	 *	@return true for AD_Client_ID, etc.
  	 */
  	public boolean isStandardColumn(String columnName )
  	{
  		if (columnName.equalsIgnoreCase(COLUMNNAME_AD_Client_ID) 
  			|| columnName.equalsIgnoreCase(COLUMNNAME_AD_Org_ID)
  			|| columnName.equalsIgnoreCase(COLUMNNAME_IsActive)
  			|| columnName.equalsIgnoreCase(COLUMNNAME_Created)
  			|| columnName.equalsIgnoreCase(COLUMNNAME_CreatedBy)
  			|| columnName.equalsIgnoreCase(COLUMNNAME_Updated)
  			|| columnName.equalsIgnoreCase(COLUMNNAME_UpdatedBy) )
  			return true;
  		
  		return false;
  	}	//	isStandardColumn
  	
	
	/**
	 * 	Get Created
	 * 	@return created
	 */
	final public Timestamp getCreated()
	{
		return (Timestamp)getValueNoCheck(COLUMNNAME_Created);
	}	//	getCreated

	/**
	 * 	Get Updated
	 *	@return updated
	 */
	final public Timestamp getUpdated()
	{
		return (Timestamp)getValueNoCheck(COLUMNNAME_Updated);
	}	//	getUpdated
	
	/**
	 * 	Get CreatedBy
	 * 	@return AD_User_ID
	 */
	final public int getCreatedBy()
	{
		Integer ii = (Integer)getValueNoCheck(COLUMNNAME_CreatedBy);
		if (ii == null)
			return 0;
		return ii.intValue();
	}	//	getCreateddBy

	/**
	 * 	Get UpdatedBy
	 * 	@return AD_User_ID
	 */
	final public int getUpdatedBy()
	{
		Integer ii = (Integer)getValueNoCheck(COLUMNNAME_UpdatedBy);
		if (ii == null)
			return 0;
		return ii.intValue();
	}	//	getUpdatedBy

	
	
	/**
	 * 	Set UpdatedBy
	 * 	@param AD_User_ID user
	 */
	final public void setUpdatedBy (int adUserID)
	{
		setValueOfColumn (COLUMNNAME_UpdatedBy, Integer.valueOf(adUserID));
	}	//	setAD_User_ID
	
	/**
	 * 	Set UpdatedBy
	 * 	@param AD_User_ID user
	 */
	final public void setCreatedBy (int adUserID)
	{
		setValueOfColumn (COLUMNNAME_CreatedBy, Integer.valueOf(adUserID));
	}	//	setAD_User_ID
	
	/**
	 *	Is Active
	 *  @return is active
	 */
	public final boolean isActive()
	{
		Boolean bb = (Boolean)getValueOfColumn(COLUMNNAME_IsActive);
		if (bb != null)
			return bb.booleanValue();
		return false;
	}	//	isActive
	
	/**
	 * 	Set Active
	 * 	@param active active
	 */
	public final void setIsActive (boolean active)
	{
		setValueOfColumn(COLUMNNAME_IsActive, Boolean.valueOf(active));
	}	//	setActive

	
	/**
	 * 
	 * @return
	 */
	public int getAD_Org_ID() {
		Integer ii = (Integer) getValueOfColumn(COLUMNNAME_AD_Org_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}

	/**
	 * 	Set AD_Org
	 * 	@param AD_Org_ID org
	 */
	final public void setAD_Org_ID (int AD_Org_ID)
	{
		setValueOfColumn (COLUMNNAME_AD_Org_ID, Integer.valueOf(AD_Org_ID));
	}	//	setAD_Org_ID

	
	/**
	 * 
	 * @return
	 */
	public int getAD_Client_ID() {
		Integer ii = (Integer) getValueOfColumn(COLUMNNAME_AD_Client_ID);
		if (ii == null)
			return 0;
		return ii.intValue();
	}
	
	public String getUUColumn() {
		String columnName = get_TableName() + "_UU";
		int index = this.get_ColumnIndex(columnName);
		return (String) getValueOfColumn(index);

		// setValueNoCheck(,uuId);
	}

	
	public void setUUColumn(String uuId) {
		String columnName = get_TableName() + "_UU";
		int index = this.get_ColumnIndex(columnName);
		if (index >= 0) {
			this.setValueOfColumn(index, uuId);
		}

		// setValueNoCheck(,uuId);
	}

	/**************************************************************************
	 * 	Set AD_Client
	 * 	@param AD_Client_ID client
	 */
	final public void setAD_Client_ID (int AD_Client_ID)
	{
		setValueOfColumn (COLUMNNAME_AD_Client_ID, Integer.valueOf(AD_Client_ID));
	}	//	setAD_Client_ID
  	

	public POInfoColumn[] getColumnsInfo() {
		return poInfo.getColumns();
	}
	
	protected void addGetActionListener(String columnName, Callable<?> callable) {
		getterMethods.put(columnName, callable);
		
	}
	
	protected <T extends Object> void addSetActionListener(String columnName, Consumer<T> consumer) {
		setterMethods.put(columnName, consumer);
		
	}
	
	/**
	 *  String representation
	 *  @return String representation
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer("PO[")
			.append(get_TableName()).append("]");
		return sb.toString();
	}	//  toString

}
