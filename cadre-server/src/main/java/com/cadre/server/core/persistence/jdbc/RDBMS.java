package com.cadre.server.core.persistence.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.persistence.exception.DBException;

public final class RDBMS {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RDBMS.class);


	/** Connection Descriptor */
	private static RDBConnection s_cc = null;

	private static Object s_ccLock = new Object();

	/** SQL Statement Separator "; " */
	public static final String SQLSTATEMENT_SEPARATOR = "; ";
	
	static {
		//  Set Default Database Connection 
		RDBMS.setDBTarget(RDBConnection.get());
	}

	/**************************************************************************
	 * Close Target
	 */
	public static void closeTarget() {

		boolean closed = false;

		// CConnection
		if (s_cc != null) {
			closed = true;
			s_cc.setDataSource(null);
		}
		s_cc = null;
		if (closed) {
			// log.fine("closed");

		}
	} // closeTarget

	/**************************************************************************
	 * Set connection
	 * 
	 * @param cc connection
	 */
	public synchronized static void setDBTarget(RDBConnection cc) {
		if (cc == null) {
			throw new IllegalArgumentException("Connection is NULL");
		}

		if (s_cc != null && s_cc.equals(cc))
			return;

		RDBMS.closeTarget();
		//
		synchronized (s_ccLock) {
			s_cc = cc;
		}

		s_cc.setDataSource();

	} // setDBTarget

	/**
	 *	Create new Connection.
	 *  The connection must be closed explicitly by the application
	 *
	 *  @param autoCommit auto commit
	 *  @param trxLevel - Connection.TRANSACTION_READ_UNCOMMITTED, Connection.TRANSACTION_READ_COMMITTED, Connection.TRANSACTION_REPEATABLE_READ, or Connection.TRANSACTION_READ_COMMITTED.
	 *  @return Connection connection
	 */
	public static Connection createConnection (boolean autoCommit, int trxLevel)
	{
		Connection conn = s_cc.getConnection (autoCommit, trxLevel);

        try {
	        if (conn != null && conn.getAutoCommit() != autoCommit)
	        {
	        	throw new IllegalStateException("Failed to set the requested auto commit mode on connection. [autoCommit=" + autoCommit +"]");
	        }
        } catch (SQLException e) {
            LOGGER.error("createConnection ("+autoCommit+","+trxLevel+")" , e);
        }

		return conn;
	}	//	createConnection
	

	   /**
     *  Create new Connection.
     *  The connection must be closed explicitly by the application
     *
     *  @param autoCommit auto commit
     *  @param trxLevel - Connection.TRANSACTION_READ_UNCOMMITTED, Connection.TRANSACTION_READ_COMMITTED, Connection.TRANSACTION_REPEATABLE_READ, or Connection.TRANSACTION_READ_COMMITTED.
     *  @return Connection connection
     */
    public static Connection createConnection (boolean autoCommit, boolean readOnly, int trxLevel)
    {
        Connection conn = s_cc.getConnection (autoCommit, trxLevel);

        if (conn == null)
        {
            throw new IllegalStateException("DB.getConnectionRO - @NoDBConnection@");
        }

        try {
	        if (conn.getAutoCommit() != autoCommit)
	        {
	        	throw new IllegalStateException("Failed to set the requested auto commit mode on connection. [autocommit=" + autoCommit +"]");
	        }
        } catch (SQLException e) {
            LOGGER.error("createConnection ("+autoCommit+","+readOnly+","+trxLevel+")" , e);

        }

        return conn;
    }   //  createConnection
    
	/**
	 * Return (pooled) r/w AutoCommit, Serializable connection. For Transaction
	 * control use Trx.getConnection()
	 * 
	 * @param createNew If true, try to create new connection if no existing
	 *                  connection
	 * @return Connection (r/w)
	 */
	public static Connection getConnectionRW(boolean createNew) {
		return createConnection(true, false, Connection.TRANSACTION_READ_COMMITTED);
	} // getConnectionRW

	/**
	 * Return everytime a new r/w no AutoCommit, Serializable connection. To be used
	 * to ID
	 * 
	 * @return Connection (r/w)
	 */
	public static Connection getConnectionID() {
		return createConnection(false, false, Connection.TRANSACTION_READ_COMMITTED);
	} // getConnectionID

	/**
	 * Return read committed, read/only from pool.
	 * 
	 * @return Connection (r/o)
	 */
	public static Connection getConnectionRO() {
		return createConnection(true, true, Connection.TRANSACTION_READ_COMMITTED); // see below
	} // getConnectionRO

	/**
	 * @return Connection (r/w)
	 */
	public static Connection getConnectionRW() {
		return getConnectionRW(true);
	}


	/**
	 * @return true, if connected to database
	 */
	public static boolean isConnected() {
		return isConnected(true);
	}

	/**
	 * Is there a connection to the database ?
	 * 
	 * @param createNew If true, try to connect it not already connected
	 * @return true, if connected to database
	 */
	public static boolean isConnected(boolean createNew) {
		// bug [1637432]
		if (s_cc == null)
			return false;

		// direct connection
		boolean success = false;

		try {
			Connection conn = getConnectionRW(createNew); // try to get a connection
			if (conn != null) {
				conn.close();
				success = true;
			} else
				success = false;
		} catch (Exception e) {
            LOGGER.error("isConnected ("+createNew+")" , e);

			success = false;
		}

		return success;
	} // isConnected
	
	
	/**************************************************************************
	 *	Get next number for Key column = 0 is Error.
	 *	@param TableName table name
	 * 	@param trxName optionl transaction name
	 *  @return next no
	 * @throws DBException 
	 */
	public static int getNextID (final String trxName, final String tableName )
	{
		if (tableName == null || tableName.length() == 0) {
			throw new IllegalArgumentException("TableName missing");
		}
		
		String sequenceName = tableName+"_sq";
		return RDBConnection.get().getDatabase().getNextID(trxName,sequenceName);

		
	}
	

    /**
     * Get int Value from sql
     * @param trxName trx
     * @param sql sql
     * @param params array of parameters
     * @return first value or -1 if not found
     */
    public static int getSQLValueEx (final String currentTrxName, final String sql, final Object... params)
    {
    	final int [] retValue = {-1};
		
		String trxName = currentTrxName;
		boolean localTrx = false;
		
		Trx trx = null;
		if (StringUtils.isBlank(trxName)) {
			localTrx = true;
			trxName = Trx.createTrxName();
			trx = Trx.get(trxName, true);
		}
		
		try (final PreparedStatement pstmt =  StatementFactory.newCPreparedStatement(trxName,sql)) {
			if (params != null && params.length > 0) {
				//
				for (int i = 0; i < params.length; i++) {
					setParameter(pstmt, i + 1, params[i]);
				}
			}
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
						retValue[0] = rs.getInt(1);
				}
			}

		} catch (SQLException e) {
			if (localTrx && trx != null) {
				trx.rollback();
				trx.close();
				trx = null;
			}
			
	        LOGGER.error("getSQLValueEx ("+sql+") - Params:" +params , e);
			throw new DBException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
		}finally {
			if (localTrx && trx != null) {
				trx.commit();
				trx.close();
			}
		}

	
    	return retValue[0];
    }
    
    /**
     * Get int Value from sql
     * @param trxName trx
     * @param sql sql
     * @param params array of parameters
     * @return first value or -1 if not found
     */
    public static String getSQLValueStringEx (final String currentTrxName, final String sql, final Object... params)
    {
    	final String [] retValue = {StringUtils.EMPTY};
		
		String trxName = currentTrxName;
		boolean localTrx = false;
		
		Trx trx = null;
		if (StringUtils.isBlank(trxName)) {
			localTrx = true;
			trxName = Trx.createTrxName();
			trx = Trx.get(trxName, true);
		}
		
		try (final PreparedStatement pstmt =  StatementFactory.newCPreparedStatement(trxName,sql)) {

			if (params != null && params.length > 0) {

				for (int i = 0; i < params.length; i++) {
					setParameter(pstmt, i + 1, params[i]);
				}
			}

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					retValue[0] = rs.getString(1);
				}
			}

		} catch (SQLException e) {
			if (localTrx && trx != null) {
				trx.rollback();
				trx.close();
				trx = null;
			}
			
			LOGGER.error("getSQLValueStringEx ("+sql+") - Params:" +params , e);

			throw new DBException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
		}finally {
			if (localTrx && trx != null) {
				trx.commit();
				trx.close();
			}
		}
		

	
    	return retValue[0];
    }
	
	
	/**
	 * Set PreparedStatement's parameter. Similar with calling
	 * <code>pstmt.setObject(index, param)</code>
	 * 
	 * @param pstmt
	 * @param index
	 * @param param
	 * @throws SQLException
	 */
	public static void setParameter(PreparedStatement pstmt, int index, Object param) throws SQLException {
		if (param == null)
			pstmt.setObject(index, null);
		else if (param instanceof String)
			pstmt.setString(index, (String) param);
		else if (param instanceof Integer)
			pstmt.setInt(index, ((Integer) param).intValue());
		else if (param instanceof BigDecimal)
			pstmt.setBigDecimal(index, (BigDecimal) param);
		else if (param instanceof Timestamp)
			pstmt.setTimestamp(index, (Timestamp) param);
		else if (param instanceof Boolean)
			pstmt.setString(index, ((Boolean) param).booleanValue() ? POModel.YES_VALUE : POModel.NO_VALUE);
		else if (param instanceof byte[])
			pstmt.setBytes(index, (byte[]) param);
		else
			throw new IllegalArgumentException("Unknown parameter type " + index + " - " + param);
	}

	/**
	 * Execute SQL Update
	 * @param currentTrxName
	 * @param sql
	 * @param params
	 * @param timeOut
	 * @return
	 * @throws DBException
	 */
	public static int executeUpdate(String currentTrxName,String sql, Object[] params,  int timeOut) {
		if (sql == null || sql.length() == 0) {
			throw new IllegalArgumentException("Required parameter missing - " + sql);
		}
		
		String trxName = currentTrxName;
		boolean localTrx = false;
		
		Trx trx = null;
		if (StringUtils.isBlank(trxName)) {
			localTrx = true;
			trxName = Trx.createTrxName();
			trx = Trx.get(trxName, true);
		}
		
		int [] response = new int[1];
		
		try (final PreparedStatement pstmt =  StatementFactory.newCPreparedStatement(trxName,sql)) {
			if (params != null && params.length > 0) {
				//
				for (int i = 0; i < params.length; i++) {
					setParameter(pstmt, i + 1, params[i]);
				}
			}
				
			//set timeout
			if (timeOut > 0)
			{
				pstmt.setQueryTimeout(timeOut);
			}

			response[0] =pstmt.executeUpdate();

		} catch (SQLException e) {
			if (localTrx && trx != null) {
				trx.rollback();
				trx.close();
				trx = null;
			}
			
            LOGGER.error("executeUpdate ("+sql+") - Params:" +params , e);
			throw new DBException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
		}finally {
			if (localTrx && trx != null) {
				trx.commit();
				trx.close();
			}
		}
		
	
		return response[0];
	}


}
