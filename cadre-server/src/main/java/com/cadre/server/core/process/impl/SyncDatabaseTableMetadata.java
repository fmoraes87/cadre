package com.cadre.server.core.process.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.annotation.SvrProcess;
import com.cadre.server.core.entity.MColumn;
import com.cadre.server.core.entity.MTable;
import com.cadre.server.core.entity.validation.ModelValidationException;
import com.cadre.server.core.persistence.exception.DBException;
import com.cadre.server.core.persistence.exception.DBNoResultException;
import com.cadre.server.core.persistence.jdbc.Trx;
import com.cadre.server.core.persistence.query.GenericCondition;
import com.cadre.server.core.persistence.query.JDBCQueryImpl;
import com.cadre.server.core.persistence.query.SearchResult;
import com.cadre.server.core.process.CadreProcess;
import com.cadre.server.core.process.ProcessInfoParameter;
import com.cadre.server.core.process.SvrProcessException;
import com.cadre.server.core.service.ModelService;
import com.cadre.server.core.util.DisplayType;

@SvrProcess(SyncDatabaseTableMetadata.SYNC_TABLE_DATABASE)
public class SyncDatabaseTableMetadata extends CadreProcess {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SyncDatabaseTableMetadata.class);


	static final String SYNC_TABLE_DATABASE = "syncTableDatabase";
	
	private static final String  DATABASE_INFORMATION_SCHEMA_TABLE_NAME = "TABLE_NAME";
	private static final String  DATABASE_INFORMATION_SCHEMA_TABLE_TYPE = "TABLE_TYPE";
	
	private static final String TABLE_TYPE="TABLE";
	private static final String VIEW_TYPE="VIEW";

	
	private static final String PARAM_TABLENAME = "tableName";
	private static final String PARAM_SYNC_FROM_DATABASE = "syncFromDatabase";
	private static final String PARAM_AD_EXTENSION = "adExtensionId";

	
	private String p_TableName;
	private boolean p_SyncFromDatabase;
	private Integer p_Extension;
	
	private ModelService service;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public String getProcessName() {
		return SYNC_TABLE_DATABASE;
	}
		

	
	@Override
	public void prepare() {
		ProcessInfoParameter[] para = getParams();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getName();
			if (para[i].getValue() == null)
				;
			else if (name.equalsIgnoreCase(PARAM_TABLENAME))
				p_TableName = (String) para[i].getValue();
			else if (name.equalsIgnoreCase(PARAM_SYNC_FROM_DATABASE))
				p_SyncFromDatabase = (Boolean) para[i].getValue();
			else if (name.equalsIgnoreCase(PARAM_AD_EXTENSION))
				p_Extension = para[i].getParameterAsInt();
			//else
			//log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		
		
	}

	@Override
	public Object doIt(String trxName){
		if (!p_SyncFromDatabase) {
			throw new SvrProcessException(Status.BAD_REQUEST.getStatusCode(), "@NotSupported@");
		}else {
			try {
				service = CDI.current().select(ModelService.class).get();

				Trx trx = Trx.get(trxName, false);
				Connection conn = trx.getConnection();
				
				DatabaseMetaData md = conn.getMetaData();
				
				try(ResultSet rs = md.getTables(conn.getCatalog(), conn.getSchema(),p_TableName.toLowerCase() , new String [] {TABLE_TYPE, VIEW_TYPE})){
					while (rs.next()) {
						String tableName = rs.getString(DATABASE_INFORMATION_SCHEMA_TABLE_NAME);
						String tableType = rs.getString(DATABASE_INFORMATION_SCHEMA_TABLE_TYPE);
						
						MTable table = null;
						try {
							table = service.getPO(trxName, MTable.TABLE_NAME,MTable.COLUMNNAME_TableName,p_TableName);
							
						}catch (DBNoResultException e) {	
							LOGGER.error("doIt() - Table not found: " +p_TableName , e);

							//Double ckeck
							if (table==null) {
								table = service.createPO(trxName, MTable.TABLE_NAME);
								table.setAD_Extension_ID(p_Extension);
								table.setName (tableName);
								table.setTableName (tableName);
								table.setIsView(VIEW_TYPE.equals(tableType));
								try {
									service.save(table);
									
								}catch (Throwable ex) {
									LOGGER.error("doIt() - Error creating table: " +p_TableName , e);
								}
								
							}
						}finally {
							addTableColumns(table);
							
						}
						
					}
				}
				
			} catch (DBException | SQLException | ModelValidationException e) {
				LOGGER.error("doIt()" , e);

				if (e instanceof SQLException) {
					throw new SvrProcessException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),e.getMessage());					
				}else if (e instanceof ModelValidationException) {
					ModelValidationException ex = (ModelValidationException) e;
					throw new SvrProcessException(ex.getStatus(),ex.getCode());					
				}else if (e instanceof DBException) {
					DBException ex = (DBException) e;
					throw new SvrProcessException(ex.getStatus(),ex.getCode());					
				}
			}
			

		}
		
		return null;
	}

	/**
	 * Create columns for table
	 * @param table
	 */
	private void addTableColumns(MTable table) {
		try {
			Trx trx = Trx.get(table.get_TrxName(), false);
			Connection conn = trx.getConnection();
			DatabaseMetaData md = conn.getMetaData();
			String tableName = table.getTableName();
			try(ResultSet rs =  md.getColumns(conn.getCatalog(), conn.getSchema(),table.getTableName().toLowerCase(), null)){
				while (rs.next ())
				{
					String tn = rs.getString ("TABLE_NAME");
					
					if (!tableName.equalsIgnoreCase (tn))
						continue;
					
					String columnName = rs.getString ("COLUMN_NAME");
					
					try {
						SearchResult<MColumn> columnsSearch = service.search(table.get_TrxName(),
								new JDBCQueryImpl.Builder(MColumn.TABLE_NAME)
									.and(GenericCondition.equals(MColumn.COLUMNNAME_ColumnName, columnName))
								    .and(GenericCondition.equals(MColumn.COLUMNNAME_AD_Table_ID, table.getAD_Table_ID()))
								 .build());

						MColumn column = columnsSearch.getSingleResult(true);
						
					}catch (DBNoResultException e) {
						LOGGER.error("addTableColumns() - Column not found: " + columnName , e);

						createColumn(table,rs);
					}
					
				}	//	while columns
				
			}
		}catch(SQLException ex) {
			LOGGER.error("addTableColumns("+ table.getTableName()+")" , ex);
			throw new DBException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),ex.getMessage());
		}
		

	}

	/**
	 * Create Column
	 * @param table
	 * @param rs
	 */
	private void createColumn(MTable table, ResultSet rs) throws SQLException {
		int dataType = rs.getInt ("DATA_TYPE");
		String typeName = rs.getString ("TYPE_NAME");
		String nullable = rs.getString ("IS_NULLABLE");
		int size = rs.getInt ("COLUMN_SIZE");
		int digits = rs.getInt ("DECIMAL_DIGITS");
		String columnName = rs.getString ("COLUMN_NAME");

		MColumn column = service.createPO(table.get_TrxName(), MColumn.TABLE_NAME);
		//
		column.setAD_Table_ID(table.getAD_Table_ID());
		column.setAD_Extension_ID(p_Extension);
		column.setColumnName (columnName);
		column.setName (columnName);
		column.setIsMandatory ("NO".equals (nullable));

		// ID
		if (columnName.toUpperCase().endsWith ("_ID")) {
			if (columnName.toUpperCase().equals(table.getTableName().toUpperCase()+"_ID")) {
				column.setAD_Reference_ID (DisplayType.ID);
				column.setIsKey(true);
			}else {
				column.setAD_Reference_ID (DisplayType.TableDir);
				
			}
			
		}
		// Date
		else if (dataType == Types.DATE || dataType == Types.TIME
			|| dataType == Types.TIMESTAMP
			// || columnName.toUpperCase().indexOf("DATE") != -1
			|| columnName.equalsIgnoreCase ("Created")
			|| columnName.equalsIgnoreCase ("Updated"))
			column.setAD_Reference_ID (DisplayType.DateTime);
		// CLOB
		else if (dataType == Types.CLOB)
			column.setAD_Reference_ID (DisplayType.Text);
		// BLOB
		else if (dataType == Types.BLOB)
			column.setAD_Reference_ID (DisplayType.Binary);
		// Boolean
		else if (size == 1
			&& (columnName.toUpperCase ().startsWith ("IS") || dataType == Types.CHAR))
			column.setAD_Reference_ID (DisplayType.YesNo);
		// List
		else if (size < 4 && dataType == Types.CHAR)
			column.setAD_Reference_ID (DisplayType.List);
		// String, Text
		else if (dataType == Types.CHAR || dataType == Types.VARCHAR
			|| typeName.startsWith ("NVAR")
			|| typeName.startsWith ("NCHAR"))
		{
			if (typeName.startsWith("N"))	//	MultiByte	
				size /= 2;
			if (size > 255)
				column.setAD_Reference_ID (DisplayType.Text);
			else
				column.setAD_Reference_ID (DisplayType.String);
		}
		// Number
		else if (dataType == Types.INTEGER || dataType == Types.SMALLINT
			|| dataType == Types.DECIMAL || dataType == Types.NUMERIC)
		{
			if (size == 10)
				column.setAD_Reference_ID (DisplayType.Integer);
			else
				column.setAD_Reference_ID (DisplayType.Amount);
		}
		else {
			column.setAD_Reference_ID (DisplayType.String);
		}

		service.save(column);

	}





}
