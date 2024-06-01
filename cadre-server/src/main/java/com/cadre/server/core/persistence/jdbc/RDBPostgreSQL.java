package com.cadre.server.core.persistence.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.persistence.exception.DBException;
import com.cadre.server.core.util.PropertiesUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class RDBPostgreSQL implements CRelationalDatabase {

	private static final Logger LOGGER = LoggerFactory.getLogger(RDBPostgreSQL.class);

	
	/** PostgreSQL ID */
	public static String DB_POSTGRESQL = "PostgreSQL";

	/** Connection Timeout in seconds */
	public static int CONNECTION_TIMEOUT = 10;
	
	/** Default Port */
	public static final int DB_POSTGRESQL_DEFAULT_PORT = 5432;

	/**
	 * PostgreSQL Database
	 */
	public RDBPostgreSQL() {
	} // DB_PostgreSQL

	/** Driver */
	private org.postgresql.Driver s_driver = null;

	/** Driver class */
	public static final String DRIVER = "org.postgresql.Driver";

	/** Default Port */
	public static final int DEFAULT_PORT = 5432;

	/** Data Source */
	private ComboPooledDataSource m_ds = null;

	/** Connection String */
	private String m_connection;

	/** Cached Database Name */
	private String m_dbName = null;

	@SuppressWarnings("unused")
	private String m_userName = null;

	/** Connection String */
	private String m_connectionURL;

	private static int m_maxbusyconnections = 0;

	public static final String NATIVE_MARKER = "NATIVE_PostgreSQL_KEYWORK";

	private Random rand = new Random();

	@Override
	public DataSource getDataSource(RDBConnection connection) {
		if (m_ds != null) {
			return m_ds;
		}

		
		
		/*int idleConnectionTestPeriod = PropertiesUtils.getIntProperty(poolProperties, "db.c3p0.idle_connection_test_period", 1200);
		int acquireRetryAttempts = PropertiesUtils.getIntProperty(poolProperties, "db.c3p0.acquire_retry_attempts", 2);
		int maxIdleTimeExcessConnections = PropertiesUtils.getIntProperty(poolProperties, "db.c3p0.max_idle_time_excess_connections", 1200);
		int maxIdleTime = PropertiesUtils.getIntProperty(poolProperties, "db.c3p0.max_idle_time", 1200);
		int unreturnedConnectionTimeout = PropertiesUtils.getIntProperty(poolProperties, "UnreturnedConnectionTimeout", 0);
		boolean testConnectionOnCheckin = PropertiesUtils.getBooleanProperty(poolProperties, "TestConnectionOnCheckin", false);
		boolean testConnectionOnCheckout = PropertiesUtils.getBooleanProperty(poolProperties, "TestConnectionOnCheckout", true);
		String mlogClass = PropertiesUtils.getStringProperty(poolProperties, "com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
		int checkoutTimeout = PropertiesUtils.getIntProperty(poolProperties, "CheckoutTimeout", 0);*/
		
		int idleConnectionTestPeriod = 1200;
		int acquireRetryAttempts =  2;
		int maxIdleTimeExcessConnections =  1200;
		int maxIdleTime =  1200;
		boolean testConnectionOnCheckin =  false;
		boolean testConnectionOnCheckout =  true;
		String mlogClass =  "com.mchange.v2.log.FallbackMLog";
		int checkoutTimeout = 0;

        try
        {
            System.setProperty("com.mchange.v2.log.MLog", mlogClass);
            //System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "ALL");
            ComboPooledDataSource cpds = new ComboPooledDataSource();
            cpds.setDataSourceName("cadreDS");
            cpds.setDriverClass(DRIVER);
            //loads the jdbc driver
            cpds.setJdbcUrl(getConnectionURL(connection));
            cpds.setUser(connection.getDbUid());
            cpds.setPassword(connection.getDbPwd());
            
            //cpds.setPreferredTestQuery(DEFAULT_CONN_TEST_SQL);
            cpds.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
            cpds.setMaxIdleTimeExcessConnections(maxIdleTimeExcessConnections);
            cpds.setMaxIdleTime(maxIdleTime);
            cpds.setTestConnectionOnCheckin(testConnectionOnCheckin);
            cpds.setTestConnectionOnCheckout(testConnectionOnCheckout);
            cpds.setAcquireRetryAttempts(acquireRetryAttempts);
            if (checkoutTimeout > 0) {
            	cpds.setCheckoutTimeout(checkoutTimeout);            	
            }

			/*int maxPoolSize = PropertiesUtils.getIntProperty(poolProperties, "db.c3p0.max_pool_size", 400);
			int initialPoolSize = PropertiesUtils.getIntProperty(poolProperties, "db.c3p0.initial_pool_size", 10);
			int minPoolSize = PropertiesUtils.getIntProperty(poolProperties, "db.c3p0.min_pool_size", 5);*/
            Map<String, String> env = System.getenv();
            String maxPoolSizeStr = env.getOrDefault("db.c3p0.max_pool_size", "15");
            int maxPoolSize =  Integer.parseInt(maxPoolSizeStr);
            
			int initialPoolSize =  10;
			int minPoolSize =  5;
			
			cpds.setInitialPoolSize(initialPoolSize);
			cpds.setMinPoolSize(minPoolSize);
			cpds.setMaxPoolSize(maxPoolSize);
			m_maxbusyconnections = (int) (maxPoolSize * 0.9);

			// statement pooling
			/*int maxStatementsPerConnection = 10;

			if (maxStatementsPerConnection > 0)
				cpds.setMaxStatementsPerConnection(maxStatementsPerConnection);
			 
			int unreturnedConnectionTimeout = 0;

            if (unreturnedConnectionTimeout > 0)
            {
	            //the following sometimes kill active connection!
	            cpds.setUnreturnedConnectionTimeout(1200);
	            cpds.setDebugUnreturnedConnectionStackTraces(true);
            }*/
            
            final Map<String, Object> extensions = new HashMap<String, Object>(4);
            extensions.put("timezone", "GMT+0");
            cpds.setExtensions(extensions);
            
            m_ds = cpds;
            m_connectionURL = m_ds.getJdbcUrl();
			
		}catch (Exception ex) {
            m_ds = null;
			LOGGER.error("Could not initialise C3P0 Datasource", ex);
        }

		return m_ds;
	}

	@Override
	public String getName() {
		return DB_POSTGRESQL;
	}

	/**
	 * Get Database Description
	 * 
	 * @return database long name and version
	 */
	public String getDescription() {
		// return s_driver.toString();
		try {
			if (s_driver == null)
				getDriver();
		} catch (Exception e) {
			LOGGER.error("getDescription()", e);

		}
		if (s_driver != null)
			return s_driver.toString();
		return "No Driver";
	
	} // getDescription

	@Override
	public java.sql.Driver getDriver() throws SQLException {
		if (s_driver == null) {
			s_driver = new org.postgresql.Driver();
			DriverManager.registerDriver(s_driver);
			DriverManager.setLoginTimeout(CONNECTION_TIMEOUT);
		}
		return s_driver;
	} // getDriver

	/**
	 * Get Standard JDBC Port
	 * 
	 * @return standard port
	 */
	@Override
	public int getStandardPort() {
		return DEFAULT_PORT;
	} // getStandardPort

	@Override
	public String getConnectionURL(RDBConnection connection) {
		// jdbc:postgresql://hostname:portnumber/databasename?encoding=UNICODE
		StringBuilder sb = new StringBuilder("jdbc:postgresql://").append(connection.getDbHost()).append(":")
				.append(connection.getDbPort()).append("/").append(connection.getDbName()).append("?encoding=UNICODE")
				.append("&currentSchema=cadre");

		String urlParameters = System.getProperty("org.cadre.postgresql.URLParameters");
		if (StringUtils.isNotEmpty(urlParameters)) {
			sb.append("&").append(urlParameters);
		}

		m_connection = sb.toString();
		return m_connection;
	}

	@Override
	public String getConnectionURL(String dbHost, int dbPort, String dbName, String userName) {
		StringBuilder sb = new StringBuilder("jdbc:postgresql://").append(dbHost).append(":").append(dbPort).append("/")
				.append(dbName)
				.append("?currentSchema=cadre");

		String urlParameters = System.getProperty("org.cadre.postgresql.URLParameters");
		if (StringUtils.isNotEmpty(urlParameters)) {
			sb.append("?").append(urlParameters);
		}

		return sb.toString();
	}

	@Override
	public String getConnectionURL(String connectionURL, String userName) {
		m_userName = userName;
		m_connectionURL = connectionURL;
		return m_connectionURL;
	}

	@Override
	public String getCatalog() {
		if (m_dbName != null)
			return m_dbName;

		return null;
	}

	@Override
	public String getSchema() {
		return "cadre";
	}

	@Override
	public boolean supportsBLOB() {
		return true;
	}

	/**
	 * String Representation
	 * 
	 * @return info
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder("DB_PostgreSQL[");
		sb.append(m_connectionURL);
		try {
			StringBuilder logBuffer = new StringBuilder(50);
			logBuffer.append("# Connections: ").append(m_ds.getNumConnections());
			logBuffer.append(" , # Busy Connections: ").append(m_ds.getNumBusyConnections());
			logBuffer.append(" , # Idle Connections: ").append(m_ds.getNumIdleConnections());
			logBuffer.append(" , # Orphaned Connections: ").append(m_ds.getNumUnclosedOrphanedConnections());
		} catch (Exception e) {
			LOGGER.error("toString()", e);
			sb.append("=").append(e.getLocalizedMessage());
		}
		sb.append("]");
		return sb.toString();
	} // toString

	@Override
	public Connection getCachedConnection(RDBConnection connection, boolean autoCommit, int transactionIsolation)
			throws Exception {
		Connection conn = null;
		Exception exception = null;
		try {
			if (m_ds == null)
				getDataSource(connection);

			//
			try {
				int numConnections = m_ds.getNumBusyConnections();
				if (numConnections >= m_maxbusyconnections && m_maxbusyconnections > 0) {
					// system is under heavy load, wait between 20 to 40 seconds
					int randomNum = rand.nextInt(40 - 20 + 1) + 20;
					Thread.sleep(randomNum * 1000);
				}
				conn = m_ds.getConnection();
				if (conn == null) {
					// try again after 10 to 30 seconds
					int randomNum = rand.nextInt(30 - 10 + 1) + 10;
					Thread.sleep(randomNum * 1000);
					conn = m_ds.getConnection();
				}

				if (conn != null) {
					if (conn.getTransactionIsolation() != transactionIsolation)
						conn.setTransactionIsolation(transactionIsolation);
					if (conn.getAutoCommit() != autoCommit)
						conn.setAutoCommit(autoCommit);
				}
			} catch (Exception e) {
				LOGGER.error("getCachedConnection", e);

				exception = e;
				conn = null;
			}

			if (conn == null && exception != null) {
				// log might cause infinite loop since it will try to acquire database
				// connection again
				/*
				 * log.log(Level.SEVERE, exception.toString()); log.fine(toString());
				 */
				System.err.println(exception.toString());
			}
		} catch (Exception e) {
			LOGGER.error("getCachedConnection", e);

			exception = e;
		}

		if (exception != null) {
			throw exception;
		}

		return conn;
	} // getCachedConnection

	/**
	 * Close
	 */
	@Override
	public void close() {
		if (m_ds != null) {
			try {
				m_ds.close();
			} catch (Exception e) {
				LOGGER.error("close()", e);
			}
		}
		m_ds = null;
	} // close
	
	@Override
	public int getNextID(String trxName, String sequenceName) {
		int nextID = -1;
		String sqlNextID =  "SELECT nextval('"+sequenceName.toLowerCase()+"')";
		try {
			nextID = RDBMS.getSQLValueEx(trxName, sqlNextID);
			
		}catch(DBException ex) {
			LOGGER.error("getNextID(" +sequenceName +")", ex);

			createSequence(sequenceName, 1, 1, Integer.MAX_VALUE, 1,trxName);
			nextID = RDBMS.getSQLValueEx(trxName,sqlNextID);			
		}
		

		return nextID;
	}
	
	public void createSequence(String name , int increment , int minvalue , int maxvalue ,int  start, String trxName)
	{
		// Check if Sequence exists
		final int cnt = RDBMS.getSQLValueEx(trxName, "SELECT COUNT(*) FROM pg_class WHERE UPPER(relname)=? AND relkind='S'", name.toUpperCase());
		final int no;
		if (start < minvalue)
			start = minvalue;
		//
		// New Sequence
		if (cnt == 0)
		{
			no = RDBMS.executeUpdate(trxName,"CREATE SEQUENCE "+name.toUpperCase()
								+ " INCREMENT BY " + increment
								+ " MINVALUE " + minvalue
								+ " MAXVALUE " + maxvalue
								+ " START WITH " + start, null,0);
		}
		//
		// Already existing sequence => ALTER
		else
		{
			no = RDBMS.executeUpdate(trxName, "ALTER SEQUENCE "+name.toUpperCase()
					+ " INCREMENT BY " + increment
					+ " MINVALUE " + minvalue
					+ " MAXVALUE " + maxvalue
					+ " RESTART WITH " + start, null,0);
		}
	}

}
