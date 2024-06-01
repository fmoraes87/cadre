package com.cadre.server.core.persistence.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RDBConnection implements Serializable, Cloneable {

	private static final Logger LOGGER = LoggerFactory.getLogger(RDBConnection.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Connection */
	private volatile static RDBConnection s_cc = null;

	/** Name of Connection */
	private String m_name = "Standard";

	/** Application Host */
	private String m_apps_host = "MyAppsServer";

	/** Database Type */
	private String m_type = "";

	/** Database Host */
	private String m_db_host ="";
	/** Database Port */
	private int m_db_port = 5432;
	/** Database name */
	private String m_db_name = "cadre";

	/** In Memory connection */
	private boolean m_bequeath = false;

	/** Connection uses Firewall */
	private boolean m_firewall = false;
	/** Firewall host */
	private String m_fw_host = "";
	/** Firewall port */
	private int m_fw_port = 0;

	/** DB User name */
	private String m_db_uid = "cadre";
	/** DB User password */
	private String m_db_pwd = "cadre";

	/** Database */
	private CRelationalDatabase m_db = null;
	/** ConnectionException */
	private Exception m_dbException = null;
	private Exception m_appsException = null;

	/** Database Connection */
	private boolean m_okDB = false;
	/** Apps Server Connection */
	private boolean m_okApps = false;

	/** Info */
	private String[] m_info = new String[2];

	/** Server Version */
	private String m_version = null;

	/** DataSource */
	private DataSource m_ds = null;

	/** DB Info */
	private String m_dbInfo = null;
	private int m_webPort;
	private int m_sslPort;
	private boolean m_queryAppsServer;

	/**
	 * Get/Set default client/server Connection
	 * 
	 * @param apps_host optional apps host for new connections
	 * @return Connection Descriptor
	 */
	public synchronized static RDBConnection get() {
		if (s_cc == null) {
			s_cc = new RDBConnection();

		}

		return s_cc;
	} // get
	
	public RDBConnection() {
		init();
	}

	private void init() {
		
		try {
			URI dbUri = new URI(System.getenv("DATABASE_URL"));
			m_db_uid = dbUri.getUserInfo().split(":")[0];
			m_db_pwd = dbUri.getUserInfo().split(":")[1];
			m_db_host = dbUri.getHost();
			m_db_port =  dbUri.getPort();
			m_db_name = dbUri.getPath().substring(1);
		    
		} catch (URISyntaxException e) {
			LOGGER.error("init()", e);
			System.exit(1);
		}
		
	}

	/**************************************************************************
	 * Create DB Connection
	 * 
	 * @return data source != null
	 */
	public boolean setDataSource() {
		if (m_ds == null) {
			CRelationalDatabase getDB = getDatabase();
			if (getDB != null) // no db selected
				m_ds = getDB.getDataSource(this);
			// System.out.println ("CConnection.setDataSource - " + m_ds);
		}
		return m_ds != null;
	} // setDataSource

	/**
	 * Set Data Source
	 * 
	 * @param ds data source
	 * @return data source != null
	 */
	public boolean setDataSource(DataSource ds) {
		if (ds == null && m_ds != null)
			getDatabase().close();
		m_ds = ds;
		return m_ds != null;
	}

	/*************************************************************************
	 * Short String representation
	 * 
	 * @return appsHost{dbHost-dbName-uid}
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder(m_apps_host);
		sb.append("{").append(m_db_host).append("-").append(m_db_name).append("-").append(m_db_uid).append("}");
		return sb.toString();
	} // toString

	/**
	 * Detail Info
	 * 
	 * @return info
	 */
	public String toStringDetail() {
		StringBuilder sb = new StringBuilder(m_apps_host);
		sb.append("{").append(m_db_host).append("-").append(m_db_name).append("-").append(m_db_uid).append("}");
		//
		Connection conn = getConnection(true, Connection.TRANSACTION_READ_COMMITTED);
		if (conn != null) {
			try {
				DatabaseMetaData dbmd = conn.getMetaData();
				sb.append("\nDatabase=" + dbmd.getDatabaseProductName() + " - " + dbmd.getDatabaseProductVersion());
				sb.append("\nDriver  =" + dbmd.getDriverName() + " - " + dbmd.getDriverVersion());
				if (isDataSource())
					sb.append(" - via DS");
				conn.close();
			} catch (Exception e) {
				LOGGER.error("init()", e);
			}
		}
		conn = null;
		return sb.toString();
	} // toStringDetail

	/**
	 * String representation. Used also for Instanciation
	 * 
	 * @return string representation
	 * @see #setAttributes(String) setAttributes
	 */
	public String toStringLong() {
		StringBuilder sb = new StringBuilder("CConnection[");
		sb.append("name=").append(escape(m_name)).append(",AppsHost=").append(escape(m_apps_host)).append(",WebPort=")
				.append(m_webPort).append(",SSLPort=").append(m_sslPort).append(",type=").append(escape(m_type))
				.append(",DBhost=").append(escape(m_db_host)).append(",DBport=").append(m_db_port).append(",DBname=")
				.append(escape(m_db_name)).append(",BQ=").append(m_bequeath).append(",FW=").append(m_firewall)
				.append(",FWhost=").append(escape(m_fw_host)).append(",FWport=").append(m_fw_port).append(",UID=")
				.append(escape(m_db_uid)).append(",PWD=").append(escape(m_db_pwd)).append("]");
		; // the format is read by setAttributes
		return sb.toString();
	} // toStringLong

	private String escape(String value) {
		if (value == null)
			return null;

		// use html like escape sequence to escape = and ,
		value = value.replace("=", "&eq;");
		value = value.replace(",", "&comma;");
		return value;
	}

	/**
	 * Get Database
	 * 
	 * @return database
	 */
	public CRelationalDatabase getDatabase() {
		// different driver
		if (m_db != null && !m_db.getName().equals(m_type)) {
			m_db = null;
		}

		if (m_db == null) {
			try {
				m_db = RDBFactory.getDatabase(m_type);
				if (m_db != null) // test class loader ability
					m_db.getDataSource(this);
			} catch (NoClassDefFoundError ee) {
				LOGGER.error("getDatabase()", ee);

			} catch (Exception e) {
				LOGGER.error("getDatabase()", e);
			}
		}
		return m_db;
	} // getDatabase

	/*************
	 * Get Database Host name
	 * 
	 * @return db host name
	 */
	public String getDbHost() {
		return m_db_host;
	} // getDbHost

	/**
	 * Set Database host name
	 * 
	 * @param db_host db host
	 */
	public void setDbHost(String db_host) {
		m_db_host = db_host;
		m_name = toString();
		m_okDB = false;
	} // setDbHost

	/**
	 * Get Database Name (Service Name)
	 * 
	 * @return db name
	 */
	public String getDbName() {
		return m_db_name;
	} // getDbName

	/**
	 * Set Database Name (Service Name)
	 * 
	 * @param db_name db name
	 */
	public void setDbName(String db_name) {
		m_db_name = db_name;
		m_name = toString();
		m_okDB = false;
	} // setDbName

	/**
	 * Get DB Port
	 * 
	 * @return port
	 */
	public int getDbPort() {
		return m_db_port;
	} // getDbPort

	/**
	 * Set DB Port
	 * 
	 * @param db_port db port
	 */
	public void setDbPort(int db_port) {
		m_db_port = db_port;
		m_okDB = false;
	} // setDbPort

	/**
	 * Set DB Port
	 * 
	 * @param db_portString db port as String
	 */
	public void setDbPort(String db_portString) {
		try {
			if (db_portString == null || db_portString.length() == 0)
				;
			else
				setDbPort(Integer.parseInt(db_portString));
		} catch (Exception e) {
			LOGGER.error("setDbPort("+ db_portString + ")", e);

		}
	} // setDbPort

	/**
	 * Has Server Connection
	 * 
	 * @return true if DataSource exists
	 */
	public boolean isDataSource() {
		return m_ds != null;
	}

	/**
	 * Get Connection String
	 * 
	 * @return connection string
	 */
	public String getConnectionURL() {
		getDatabase(); // updates m_db
		if (m_db != null)
			return m_db.getConnectionURL(this);
		else
			return "";
	} // getConnectionURL

	/**
	 * Create Connection - no not close. Sets m_dbException
	 * 
	 * @param autoCommit           true if autocommit connection
	 * @param transactionIsolation Connection transaction level
	 * @return Connection
	 */
	public Connection getConnection(boolean autoCommit, int transactionIsolation) {
		Connection conn = null;
		m_dbException = null;
		m_okDB = false;
		//
		getDatabase(); // updates m_db
		if (m_db == null) {
			LOGGER.error("No Database Connector");
			m_dbException = new IllegalStateException("No Database Connector");
			return null;
		}
		//

		try {
			// if (!Ini.isClient() // Server
			// && trxLevel != Connection.TRANSACTION_READ_COMMITTED) // PO_LOB.save()
			// {
			// Exception ee = null;
			try {
				conn = m_db.getCachedConnection(this, autoCommit, transactionIsolation);
			} catch (Exception e) {
				LOGGER.error("getConnection()", e);
			}
			// Verify Connection
			if (conn != null) {
				if (conn.getTransactionIsolation() != transactionIsolation)
					conn.setTransactionIsolation(transactionIsolation);
				if (conn.getAutoCommit() != autoCommit)
					conn.setAutoCommit(autoCommit);
				m_okDB = true;
			}else {
				LOGGER.error("getConnection() => Null");
			}
		} catch (UnsatisfiedLinkError ule) {
			String msg = ule.getLocalizedMessage() + " -> Did you set the LD_LIBRARY_PATH ? - " + getConnectionURL();
			m_dbException = new Exception(msg);
			LOGGER.error(msg);

		} catch (SQLException ex) {
			m_dbException = ex;
			LOGGER.error(getConnectionURL() + " - " + ex.getLocalizedMessage(),ex.getMessage());
		}
		// System.err.println ("CConnection.getConnection - " + conn);
		return conn;
	} // getConnection

	/**
	 * Get Database User
	 * 
	 * @return db user
	 */
	public String getDbUid() {
		return m_db_uid;
	} // getDbUid

	/**
	 * Set Database User
	 * 
	 * @param db_uid db user id
	 */
	public void setDbUid(String db_uid) {
		m_db_uid = db_uid;
		m_name = toString();
		m_okDB = false;
	} // setDbUid

	/**
	 * Get Database Password
	 * 
	 * @return db password
	 */
	public String getDbPwd() {
		return m_db_pwd;
	} // getDbPwd

	/**
	 * Set DB password
	 * 
	 * @param db_pwd db user password
	 */
	public void setDbPwd(String db_pwd) {
		m_db_pwd = db_pwd;
		m_okDB = false;
	} // setDbPwd

}
