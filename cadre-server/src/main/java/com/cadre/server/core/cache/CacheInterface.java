package com.cadre.server.core.cache;

public interface CacheInterface {

	/**
	 *	Reset Cache
	 *	@return number of items reset
	 */
	public int reset();
	
	/**
	 *	Reset Cache
	 *	@return number of items reset
	 */
	public int reset(int recordId);

	/**
	 * 	Get Size of Cache
	 *	@return number of items
	 */
	public int size();

	/**
	 * New record created notification 
	 * @param record_ID
	 */
	public void newRecord(int record_ID);
	
}
