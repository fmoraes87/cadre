package com.cadre.server.core.persistence;

import com.cadre.server.core.entity.POInfo;
import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.persistence.query.SearchQuery;
import com.cadre.server.core.persistence.query.SearchResult;


public interface GenericDAO {

	/**************************************************************************
	 * 
	 * @param trxName
	 * @param tableName
	 * @param criteria
	 * @return
	 */
	<T extends POModel> SearchResult<T> getModels(String trxName, SearchQuery searchQuery);

	/**************************************************************************
	 * Update Value or create new record. To reload call load() - not updated
	 * @param trxName transaction name
	 * @param model   model
	 */
	<T extends POModel> void save(T model) ;

	/**************************************************************************
	 * Delete Current Record
	 * 
	 * @param trxName transaction name
	 * @param model   model
	 * @param force   delete also processed records
	 * @return true if deleted
	 */
	<T extends POModel> void delete(T model, boolean force);
	
	
	/**
	 * Get POInfo for table
	 * @param tableName
	 * @return
	 */
	POInfo getPOInfo(String tableName);
	
	/**
	 * Get next ID
	 * @param trxName
	 * @param tableName
	 * @return
	 */
	int getNextID(String trxName, String tableName);
	

}
