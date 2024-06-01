package com.cadre.server.core.persistence.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.EmptyStackException;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.persistence.TrxBody;
import com.cadre.server.core.persistence.exception.DBException;

public class Trx {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(Trx.class);

	
	private static final Map<String,Trx> s_cache = new ConcurrentHashMap<String, Trx>(); 
	
	private	Connection 	m_connection = null;
	private	String 		m_trxName = null;
	private boolean		m_active = false;
	private Stack<Savepoint>   savePoints = new Stack<Savepoint>();
	private int chainSize = 0 ;


	private long m_startTime;

	/** transaction timeout, in seconds **/
	private int m_timeout = 60 * 120; //120 minutes
	
	//protected Exception trace;
	
	/**
	 * 	Create unique Transaction Name
	 *	@return unique name
	 */
	public static String createTrxName ()
	{
		return createTrxName(null);
	}	
	
	/**
	 * 	Create unique Transaction Name
	 *	@param prefix optional prefix
	 *	@return unique name
	 */
	public static String createTrxName (String prefix)
	{
		
		if (prefix == null || prefix.length() == 0) {
			prefix = "Trx";			
		}
		prefix += "_" + UUID.randomUUID(); //System.currentTimeMillis();

		StringBuilder l_trxname = new StringBuilder(prefix);
		if (l_trxname.length() > 23) {
			l_trxname.setLength(23);
		}
			
		prefix = l_trxname.toString();
		return prefix;
	}	//	createTrxName
	
	

	/**
	 * 	Get Transaction
	 *	@param trxName trx name
	 *	@param createNew if false, null is returned if not found
	 *	@return Transaction or null
	 */
	public static Trx get (String trxName, boolean createNew)
	{

		if ((trxName == null || trxName.length() == 0)) {
			throw new IllegalArgumentException ("No Transaction Name");
		}else if (trxName != null && trxName.length() > 0) {
			Trx retValue  = (Trx)s_cache.get(trxName);

			if (retValue == null && createNew)
			{
				retValue = new Trx (trxName);
				s_cache.put(trxName, retValue);
			}	
			
			return retValue;
		}

		throw new IllegalArgumentException();
	}	//	get

	/**************************************************************************
	 * 	Transaction Constructor
	 * 	@param trxName unique name
	 */
	private Trx (String trxName)
	{
		this (trxName, null);
	}	//	Trx

	/**
	 * 	Transaction Constructor
	 * 	@param trxName unique name
	 *  @param con optional connection ( ignore for remote transaction )
	 * 	 */
	private Trx (String trxName, Connection con)
	{
	//	log.info (trxName);
		setTrxName (trxName);
		if (trxName.length() < 36)
		{
			String msg = "Illegal transaction name format, not prefix+UUID or UUID: " + trxName;
			//log.log(Level.SEVERE, msg, new Exception(msg));
		}
		setConnection (con);
	}	//	Trx
	
	/**
	 * 	Get Name
	 *	@return name
	 */
	public String getTrxName()
	{
		return m_trxName;
	}	//	getName

	

	/**
	 * 	Set Connection
	 *	@param conn connection
	 */
	private void setConnection (Connection conn)
	{
		if (conn == null)
			return;
		m_connection = conn;
		//if (log.isLoggable(Level.FINEST)) log.finest("Connection=" + conn);
		try
		{
			m_connection.setAutoCommit(false);
		}
		catch (SQLException e)
		{
			LOGGER.error("setConnection", e);
		}
		//trace = new Exception();
	}	//	setConnection
	
	/**
	 * 	Set Trx Name
	 *	@param trxName transaction name
	 */
	private void setTrxName (String trxName)
	{
		if (trxName == null || trxName.length() == 0)
			throw new IllegalArgumentException ("No Transaction Name");
		m_trxName = trxName;
	}	//	setName
	
		
	/**
	 * 	Rollback
	 *  @param throwException if true, re-throws exception
	 *	@return true if success, false if failed or transaction already rollback
	 */
	public synchronized boolean rollback(boolean throwException)
	{
		//local
		try
		{
			if (m_connection != null)
			{
				m_connection.rollback();
				//log.log(isLocalTrx(m_trxName) ? Level.FINE : Level.INFO, "**** " + m_trxName);
				m_active = false;
				//fireAfterRollbackEvent(true);
				return true;
			}
		}
		catch (SQLException e)
		{
			LOGGER.error("rollback", e);

			if (throwException)
			{
				m_active = false;
				//fireAfterRollbackEvent(false);
				throw new DBException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
			}
		}		
		m_active = false;
		//fireAfterRollbackEvent(false);
		return false;
	}	//	rollback
	
	/**
	 * Release Savepoint
	 * @throws SQLException
	 * @see {@link Connection#releaseSavepoint(Savepoint)}
	 */
	public synchronized void releaseSavepoint()
	{
		if (m_connection == null) 
		{
			getConnection();
		}
		if(m_connection != null)
		{
			try {
				if (!savePoints.isEmpty()) {
					Savepoint currentSavepoint = savePoints.pop();
					try {
						if (m_connection != null) {
							m_connection.releaseSavepoint(currentSavepoint);
						}
						
					} catch (SQLException e) {
						LOGGER.error("releaseSavepoint()", e);
						
						throw new DBException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
					}
					
				}
				
			}catch(EmptyStackException ex) {
				LOGGER.error("releaseSavepoint()", ex);

				return;
			}
			
		}
		
	}

	/**
	 * Commit
	 * @return true if success
	 */
	public boolean commit()
	{
		try 
		{
			return commit(false);
		} 
		catch(SQLException e) 
		{
			LOGGER.error("commit()", e);

			return false;
		}
	}
	
	/**
	 * Commit
	 * @param throwException if true, re-throws exception
	 * @return true if success
	 **/
	public synchronized boolean commit(boolean throwException) throws SQLException
	{
		//local
		try
		{
			if (m_connection != null)
			{
				m_connection.commit();
			//	if (log.isLoggable(Level.INFO)) log.info ("**** " + m_trxName);
				m_active = false;
				//fireAfterCommitEvent(true);
				return true;
			}
		}
		catch (SQLException e)
		{
			LOGGER.error("commit(throwException)", e);

			if (throwException) 
			{
				m_active = false;
			//	fireAfterCommitEvent(false);
				throw e;
			}
			else
			{
				String msg = DBException.getDefaultDBExceptionMessage(e);
				//log.saveError(msg != null ? msg : e.getLocalizedMessage(), e);
			}
		}
		m_active = false;
		//fireAfterCommitEvent(false);
		return false;
	}	//	commit
	

	/**
	 * 	Rollback
	 *  @param throwException if true, re-throws exception
	 *	@return true if success, false if failed or transaction already rollback
	 */
	public boolean rollback()
	{
		try {
			if (!savePoints.isEmpty()) {
				Savepoint currentSavepoint = savePoints.pop();
				try {
					if (m_connection != null) {
						m_connection.rollback(currentSavepoint);
						// if (log.isLoggable(Level.INFO)) log.info ("**** " + m_trxName);
						return true;
					}
				} catch (SQLException e) {
					LOGGER.error("rollback()", e);
					throw new DBException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
				}
				return false;
				
				
			}
			
			return rollback(false);
			
		}catch(EmptyStackException ex) {
			LOGGER.error("rollback()", ex);
			return rollback(false);
		}
	}	//	rollback
	
	/**
	 * Get connection
	 * @return connection
	 */
	public Connection getConnection()
	{
		return getConnection(true);
	}
	
	/**
	 * 	Get Connection
	 *  @param createNew if true, create new connection if the trx does not have one created yet
	 *	@return connection
	 */
	public synchronized Connection getConnection(boolean createNew)
	{
		//if (log.isLoggable(Level.ALL))log.log(Level.ALL, "Active=" + isActive() + ", Connection=" + m_connection);

		if (m_connection == null)	//	get new Connection
		{
			if (createNew)
			{
				if (!s_cache.containsKey(m_trxName))
				{
					new Exception("Illegal to getConnection for Trx that is not register.").printStackTrace();
					return null;
				}
				setConnection(RDBMS.createConnection(false, Connection.TRANSACTION_READ_COMMITTED));
			}
			else
				return null;
		}
		if (!isActive()) {
			start();			
		}
		//if (MSysConfig.getBooleanValue(MSysConfig.TRACE_ALL_TRX_CONNECTION_GET, false))
		//	trace = new Exception();
		return m_connection;
	}	//	getConnection

	/**
	 * 	Start Trx
	 *	@return true if trx started
	 */
	public boolean start()
	{
		if (m_active)
		{
		//	log.warning("Trx in progress " + m_trxName);
			return false;
		}
		m_active = true;
		m_startTime = System.currentTimeMillis();
		return true;
	}	//	startTrx
	
	/**
	 * 	Transaction is Active
	 *	@return true if transaction active  
	 */
	public boolean isActive()
	{
		return m_active;
	}	//	isActive



	/**
	 * End Transaction and Close Connection
	 * 
	 * @return true if success
	 */
	public synchronized boolean close() {
		s_cache.remove(m_trxName);

		// local
		if (m_connection == null) {
			return true;			
		}

		if (isActive()) {
			commit();			
		}

		// Close Connection
		try {
			m_connection.setAutoCommit(true);
		} catch (SQLException e) {
			LOGGER.error("close()", e);
			
		} finally {
			try {
				m_connection.close();
			} catch (SQLException e) {
				LOGGER.error("close()", e);
			}
		}
		m_connection = null;

		return true;
	} // close


	/**
	 * 
	 * @param name
	 * @return Savepoint
	 */
	public synchronized void setSavepoint(String name)  {
		if (m_connection == null) {
			getConnection();			
		}
		try {
			if(m_connection != null) {
				Savepoint savePoint;
				if (name != null) {
					savePoint = m_connection.setSavepoint(name);
				}else {
					savePoint = m_connection.setSavepoint();
				}
				savePoints.push(savePoint);
			}
		}catch (SQLException e) {
			LOGGER.error("setSavepoint(" +name + ")", e);

			throw new DBException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
		}
				
	}



	public void execute(TrxBody body) {	
		try {	
			chainSize++;
			if (chainSize> 1) {
				setSavepoint(null);
			}
			body.run();	
		}catch(Throwable ex) {
			LOGGER.error("execute(body)", ex);

			this.rollback();
			chainSize--;
		}
	}



	/*public Trx commitAndClose(boolean localTrx) {
		if (localTrx) {
			commit();
			close();
		}else {
			chainSize--;
			releaseSavepoint();
		}
		
		return this;
	}*/

}
