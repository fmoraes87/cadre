package com.cadre.server.core.persistence.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.cadre.server.core.persistence.exception.DBException;

public interface CRelationalDatabase {

	/**
	 * Close
	 */
	public void close();

	/**
	 * Get Cached Connection on Server
	 * 
	 * @param connection           info
	 * @param autoCommit           true if autocommit connection
	 * @param transactionIsolation Connection transaction level
	 * @return connection or null
	 * @throws Exception
	 */
	public Connection getCachedConnection(RDBConnection connection, boolean autoCommit, int transactionIsolation)
			throws Exception;

	/**
	 * Create DataSource
	 * 
	 * @param connection connection
	 * @return data dource
	 */
	public DataSource getDataSource(RDBConnection connection);

	/**
	 * Get Database Name
	 * 
	 * @return database short name
	 */
	public String getName();

	/**
	 * Get Database Description
	 * 
	 * @return database long name and version
	 */
	public String getDescription();

	/**
	 * Get and register Database Driver
	 * 
	 * @return Driver
	 * @throws SQLException
	 */
	public Driver getDriver() throws SQLException;

	/**
	 * Get Standard JDBC Port
	 * 
	 * @return standard port
	 */
	public int getStandardPort();

	/**
	 * Get Database Connection String
	 * 
	 * @param connection Connection Descriptor
	 * @return connection String
	 */
	public String getConnectionURL(RDBConnection connection);

	/**
	 * Get Connection URL
	 * 
	 * @param dbHost   db Host
	 * @param dbPort   db Port
	 * @param dbName   db Name
	 * @param userName user name
	 * @return url
	 */
	public String getConnectionURL(String dbHost, int dbPort, String dbName, String userName);

	/**
	 * Get Database Connection String
	 * 
	 * @param connectionURL Connection URL
	 * @param userName      user name
	 * @return connection String
	 */
	public String getConnectionURL(String connectionURL, String userName);

	/**
	 * Get JDBC Catalog
	 * 
	 * @return catalog
	 */
	public String getCatalog();

	/**
	 * Get JDBC Schema
	 * 
	 * @return schema
	 */
	public String getSchema();

	/**
	 * Supports BLOB
	 * 
	 * @return true if BLOB is supported
	 */
	public boolean supportsBLOB();

	/**
	 * String Representation
	 * 
	 * @return info
	 */
	public String toString();

	public int getNextID(String trxName, String sequenceName) ;
	

}
