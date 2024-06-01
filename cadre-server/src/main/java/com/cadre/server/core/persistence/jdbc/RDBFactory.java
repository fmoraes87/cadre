package com.cadre.server.core.persistence.jdbc;

public class RDBFactory {

	private static CRelationalDatabase datasource = new RDBPostgreSQL();
	/**
	 *  Get Database by database Id.
	 *  @return database
	 */
	public static CRelationalDatabase getDatabase (String type)
	{
		return datasource;
	}
}
