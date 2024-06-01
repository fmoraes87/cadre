package com.cadre.server.core.persistence;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.cache.CCache;
import com.cadre.server.core.entity.AccessLevel;
import com.cadre.server.core.entity.POInfo;
import com.cadre.server.core.entity.POInfoColumn;
import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.persistence.exception.DBException;
import com.cadre.server.core.persistence.exception.DBPersistentObjectException;
import com.cadre.server.core.persistence.exception.DBQuerySyntaxException;
import com.cadre.server.core.persistence.jdbc.JDBCUtilities;
import com.cadre.server.core.persistence.jdbc.RDBMS;
import com.cadre.server.core.persistence.jdbc.StatementFactory;
import com.cadre.server.core.persistence.jdbc.Trx;
import com.cadre.server.core.persistence.query.JDBCSearchQuery;
import com.cadre.server.core.persistence.query.SearchQuery;
import com.cadre.server.core.persistence.query.SearchResult;
import com.cadre.server.core.resolver.ModelResolver;

@Singleton
public final class RDBMSGenericDAO implements GenericDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(RDBMSGenericDAO.class);

	
	private static CCache<String, POInfo> poInfoCache = new CCache<>("POInfo", 50);
	
	public RDBMSGenericDAO() {
	}

	@Override
	public SearchResult getModels(String currentTrxName, SearchQuery searchQuery){
		if (searchQuery!=null && searchQuery instanceof JDBCSearchQuery) {
			return this.getModels(currentTrxName,(JDBCSearchQuery) searchQuery);			
		}else {
			throw new IllegalArgumentException("SearchQuery isn't an instanceof JDBCSearchQuery");
		}
	}
	
	public SearchResult getModels(String currentTrxName, JDBCSearchQuery searchQuery){
		final List<POModel> result = new LinkedList<>();
		
		String trxName = currentTrxName;
		boolean localTrx = false;
		
		Trx trx = null;
		if (StringUtils.isBlank(trxName)) {
			localTrx = true;
			trxName = Trx.createTrxName();
			trx = Trx.get(trxName, true);
		}

		POInfo poInfo = getPOInfo(searchQuery.getTableName());
		try (final PreparedStatement pstmt = searchQuery.getCPreparedStatement(trxName,poInfo)){

			try (ResultSet rs = pstmt.executeQuery()){
				
				while (rs.next()) {
					POModel po = load(searchQuery.getTableName(),rs);
					po.set_TrxName(trxName);
					result.add(po);							
				}
			}
		} catch (SQLException e) {
			if (localTrx && trx != null) {
				trx.rollback();
				trx.close();
				trx = null;
			}
			
			LOGGER.error("getModels("+poInfo+")", e);
			throw new DBException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),e.getMessage());
		} finally {
			if (localTrx && trx != null) {
				trx.commit();
				trx.close();
			}
		}
	
		return new SearchResult(result);
	}

	/**
	 * Method responsable for read the result set and create instance of a POModel
	 * @param tableName
	 * @param rs
	 * @return
	 */
	private POModel load(String tableName, ResultSet rs) {
		
		try {
			POModel poModel = ModelResolver.get().resolve(tableName);
			POInfo poInfo =poModel.getPOInfo()!=null? poModel.getPOInfo(): getPOInfo(tableName);
			poModel.setPoInfo(poInfo);
			
			int size = poInfo.getColumnCount();
			int index = 0;
			
			//  load column values
			for (index = 0; index < size; index++)
			{
				String columnName = poInfo.getColumnName(index);
				Class<?> clazz = poInfo.getColumnClass(index);
				
				if (clazz == Integer.class) {
					poModel.loadInicialValueFromDB(index, Integer.valueOf(rs.getInt(columnName)));
				} else if (clazz == BigDecimal.class) {
					poModel.loadInicialValueFromDB(index, rs.getBigDecimal(columnName));
				} else if (clazz == Boolean.class) {
					poModel.loadInicialValueFromDB(index, Boolean.valueOf(POModel.YES_VALUE.equals(rs.getString(columnName))));
				} else if (clazz == Timestamp.class) {
					poModel.loadInicialValueFromDB(index, rs.getTimestamp(columnName));
					//} else if (DisplayType.isLOB(poInfo.getColumnDisplayType(index))) {
					// poModel.loadInicialValueFromDB(index,get_LOB(rs.getObject(columnName));
				} else if (clazz == String.class) {
					poModel.loadInicialValueFromDB(index, rs.getString(columnName));
				} else {
					// poModel.loadInicialValueFromDB(index, loadSpecial(rs, index));
				}
				// NULL
				if (rs.wasNull()) {
					poModel.loadInicialValueFromDB(index, null);
					
				}
				//
				
			}
			return poModel;
			
		} catch (SQLException e) {
			LOGGER.error("load("+tableName+")", e);

			throw new DBQuerySyntaxException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),e.getSQLState(),e.getMessage());

		}

	}



	/**
	 * Method responsable to load a POInfo for a table
	 * @param tableName 
	 * @return POInfo
	 * @throws DBException 
	 */
	@Override
	public POInfo getPOInfo(String tableName) {
		
		if (poInfoCache.containsKey(tableName)) {
			return poInfoCache.get(tableName);
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append( "SELECT t.AD_Table_ID,c.ColumnName,c.AD_Reference_ID," ); //1..3
		sql.append( "	c.IsMandatory, " ); //4
		sql.append( "	c.Name, c.Description,c.AD_Column_ID, " );//5..7
		sql.append( "	c.isKey, c.isTranslatable, " ); //8..9
		sql.append( "	t.isTranslated, " ); //10
		sql.append( "	t.accessLevel, " ); //11
		sql.append( "	c.updatable, " ); //12
		sql.append( "	c.isIdentifier " ); //13


		sql.append(  "FROM  AD_Table t inner join AD_Column c ");
		sql.append("	ON (t.AD_Table_ID=c.AD_Table_ID) ");
		sql.append(  " where t.tableName = ? and c.isActive='Y' ");
		
		Trx trx = Trx.get(Trx.createTrxName(), true);
		
		try (final PreparedStatement pstmt = StatementFactory.newCPreparedStatement(trx.getTrxName(),sql.toString())){
			pstmt.setString(1, tableName);
			
			try (ResultSet rs = pstmt.executeQuery()){
				List<POInfoColumn> list = new LinkedList<POInfoColumn>();
				int tableID = 0;
				boolean isTranslated=false;
				String accessLevel = AccessLevel.ACCESSLEVEL_Organization.getCode();
				
				while (rs.next()) {
					if (tableID == 0) {
						tableID = rs.getInt(1);						
					}
					
					String columnName = rs.getString(2);
					int adReferenceID = rs.getInt(3);
					boolean isMandatory = POModel.YES_VALUE.equals(rs.getString(4));		
					
					String name = rs.getString(5);
					String description = rs.getString(6);
					int adColumnID = rs.getInt(7);
					boolean isKey = POModel.YES_VALUE.equals(rs.getString(8));
					boolean isTranslatable = POModel.YES_VALUE.equals(rs.getString(9));
					boolean isUpdtable = POModel.YES_VALUE.equals(rs.getString(12));
					boolean isIdentifier = POModel.YES_VALUE.equals(rs.getString(13));
					POInfoColumn col = new POInfoColumn (
							adColumnID, columnName, adReferenceID,
						isMandatory,
						name, description,
						isKey, isTranslatable,isUpdtable,isIdentifier);
						
					list.add(col);
					
					//TODO not so good
					accessLevel = rs.getString(11);
					isTranslated = POModel.YES_VALUE.equals(rs.getString(10));
				
				}
				
				POInfo poInfo = new POInfo(tableID,isTranslated,tableName,AccessLevel.getAccessLevelByCode(accessLevel),list);
				poInfoCache.put(tableName, poInfo);
				
			} 
			
		} catch (SQLException e) {
			trx.rollback();
			LOGGER.error("getPOInfo("+tableName+")", e);
			throw new DBException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),e.getSQLState(),e.getMessage());
		}finally {
			if (trx != null) {
				trx.commit();
				trx.close();
			}
		}
			
		
		return poInfoCache.get(tableName);
		
		
		

	}
	
	@Override
	public void save(POModel po) {
		if (po != null) {
			if (po.isNew()) {
				createModel(po);
			} else if (po.isChanged()) {
				updatePOModel(po);
			} else {
				// DBPersistentObjectException
				return;
			}

		} else {
			throw new IllegalArgumentException("po=null");
		}

	}
	
	/**
	 * 
	 * @param trxName
	 * @param po
	 * @throws DBException 
	 */
	private void createModel(POModel po) {
		
		String trxName = po.get_TrxName();
		boolean localTrx = false;
		
		Trx trx = null;
		if (StringUtils.isBlank(trxName)) {
			localTrx = true;
			trxName = Trx.createTrxName();
			trx = Trx.get(trxName, true);
		}
		
		POInfo poInfo = getPOInfo(po.get_TableName());
		if (!po.get_TableName().endsWith("_Trl")) {
			po.setKeyValue(getNextID(trxName,po.get_TableName()));			
		}

		StringBuilder insertSQLBuilder = new StringBuilder("INSERT INTO  "+po.get_TableName() + " ( ");
		List<POInfoColumn> columns =  Arrays.asList(poInfo.getColumns());
		List<String> insertColumn = new ArrayList<>();
		columns.stream().map(c -> c.columnName).forEach(columnName -> {
			if (po.getValueOfColumn(columnName)!=null) {
				insertColumn.add(columnName);
			}
		});

		
		insertSQLBuilder.append(StringUtils.join(insertColumn.stream().iterator(),','));
		insertSQLBuilder.append(" ) VALUES ( " );
		insertSQLBuilder.append(StringUtils.repeat("?",  ",", insertColumn.size()));
		insertSQLBuilder.append(")") ;		
		
		try (final PreparedStatement pstmt =  StatementFactory.newCPreparedStatement(trxName,insertSQLBuilder.toString())) {

			int index = 1;
			for (String columnName : insertColumn) {
				JDBCUtilities.configPreparedStatement(pstmt, po.getPOInfo(), index, columnName, po.getValueOfColumn(columnName));
				index++;
			}
			int n = pstmt.executeUpdate();
			if (n == 0) {
				throw new DBPersistentObjectException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),"@ErrorCreateRecord@");
			}

		} catch (SQLException e) {
			if (localTrx && trx != null) {
				trx.rollback();
				trx.close();
				trx = null;
			}
			LOGGER.error("createModel("+poInfo+")", e);
			throw new DBPersistentObjectException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),e.getSQLState(),e.getMessage());
		}finally {
			if (localTrx && trx != null) {
				trx.commit();
				trx.close();
			}
		}
		
		
   
	}

	public int getNextID(String trxName, String tableName) {
		return RDBMS.getNextID(trxName, tableName);
	}

	/**
	 * Update POModel
	 * @param po
	 * @throws DBException 
	 * @throws DBQuerySyntaxException 
	 */
	private void updatePOModel(POModel po){
		
		String trxName = po.get_TrxName();
		boolean localTrx = false;
		
		Trx trx = null;
		if (StringUtils.isBlank(trxName)) {
			localTrx = true;
			trxName = Trx.createTrxName();
			trx = Trx.get(trxName, true);
		}
		
		List<String> updatedColumns = new ArrayList<>();
		for (POInfoColumn c: po.getColumnsInfo()) {
			if (c.isKey) {
				continue;
			}
			
			if (po.isValueChanged(c.columnName)) {
				updatedColumns.add(c.columnName);
			}
			
		}
		StringBuilder updateSQLBuilder = new StringBuilder("UPDATE "+po.get_TableName() + " SET ");
		updateSQLBuilder.append((StringUtils.join(updatedColumns.stream().map(n -> n + " = ? " ).toArray(n -> new String[n]), ',')));
		String columnNameID = po.get_TableName()+ "_ID";
		updateSQLBuilder.append(" WHERE " + columnNameID + " = ? ");
		try (final PreparedStatement pstmt = StatementFactory.newCPreparedStatement(trxName,updateSQLBuilder.toString())){
			int index = 1;
			for (String columnName: updatedColumns) {
				JDBCUtilities.configPreparedStatement(pstmt, po.getPOInfo(), index, columnName, po.getValueOfColumn(columnName));
				index++;
			}
				
			JDBCUtilities.configPreparedStatement(pstmt, po.getPOInfo(), index, columnNameID, po.getValueOfColumn(columnNameID));
				
			int n = pstmt.executeUpdate();
			if (n == 0) {
				throw new DBPersistentObjectException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),"@ErrorUpdateRecord@","No record updated with id eq " + po.getValueOfColumn(columnNameID));
			}
		
		} catch (SQLException e) {
			if (localTrx && trx != null) {
				trx.rollback();
				trx.close();
				trx = null;
			}
			LOGGER.error("updatePOModel()- " + updateSQLBuilder.toString(), e);
			throw new DBPersistentObjectException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),e.getSQLState(),e.getMessage());
		} finally {
			if (localTrx && trx != null) {
				trx.commit();
				trx.close();
			}
		}
		

	}

	@Override
	public void delete(POModel po, boolean force) {
		
		String trxName = po.get_TrxName();
		
		boolean localTrx = false;
		
		Trx trx = null;
		if (StringUtils.isBlank(trxName)) {
			localTrx = true;
			trxName = Trx.createTrxName();
			trx = Trx.get(trxName, true);
		}

		String columnNameID = po.get_TableName()+ "_ID";
		
		StringBuilder deleteSQLBuilder = new StringBuilder("DELETE FROM ");
		deleteSQLBuilder.append(po.get_TableName() );
		deleteSQLBuilder.append(" WHERE " + columnNameID + " = ? ");
		
		//delete cascade TODO
		//delete Trl TODO
		
		try (final PreparedStatement pstmt = StatementFactory.newCPreparedStatement(trxName,deleteSQLBuilder.toString())){
			int index = 1;
			JDBCUtilities.configPreparedStatement(pstmt, po.getPOInfo(), index, columnNameID, po.getValueOfColumn(columnNameID));
			
			int n = pstmt.executeUpdate();
			if (n == 0) {
				throw new DBPersistentObjectException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),"@ErrorDeleteRecord@","No record deleted with id eq " + po.getValueOfColumn(columnNameID));
			}
			
		} catch (SQLException e) {
			if (localTrx && trx != null) {
				trx.rollback();
				trx.close();
				trx = null;
			}
			LOGGER.error("delete()- " + deleteSQLBuilder.toString(), e);
			throw new DBPersistentObjectException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),e.getSQLState(),e.getMessage());
		} finally {
			if (localTrx && trx != null) {
				trx.commit();
				trx.close();
			}
		}
		
	}

}
