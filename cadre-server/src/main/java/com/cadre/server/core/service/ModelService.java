package com.cadre.server.core.service;

import com.cadre.server.core.boundary.ServiceProvider;
import com.cadre.server.core.entity.POInfo;
import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.persistence.query.SearchQuery;
import com.cadre.server.core.persistence.query.SearchResult;

public interface ModelService extends ServiceProvider {

	/**
	 * 
	 * @param <T>
	 * @param trxName
	 * @param genericSearchQuery
	 * @return
	 */
	public <T extends POModel> SearchResult<T> search(final String trxName, final SearchQuery genericSearchQuery);
	
	/**
	 * 
	 * @param <T>
	 * @param trxName
	 * @param tablename
	 * @param columnName
	 * @param proprety
	 * @return
	 */
	public <T extends POModel> T getPO(final String trxName, final String tablename, final String columnName, final Object proprety);

	/**
	 * 
	 * @param <T>
	 * @param trxName
	 * @param tablename
	 * @param id
	 * @return
	 */
	public <T extends POModel> T getPO(final String trxName, final String tablename, final Object id);
	
	/**
	 * 
	 * @param <T>
	 * @param trxName
	 * @param po
	 */
	public <T extends POModel> void save(T po) ;
	
	/**
	 * 
	 * @param <T>
	 * @param trxName
	 * @param po
	 */
	public <T extends POModel> void delete(T po) ;
	
	/**
	 * 
	 * @param tablename
	 * @return
	 */
	public POInfo getPOInfo(final String tablename) ;
	

	public void deleteAll(String trxName, SearchQuery query);

	public <T extends POModel> T createPO(String trxName,String tableName);


}
