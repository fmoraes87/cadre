package com.cadre.server.core.persistence.exception;

import java.sql.SQLException;

import com.cadre.server.core.exception.CadreException;

public class DBException extends CadreException {

	public static final String DATABASE_OPERATION_TIMEOUT_MSG = "DatabaseOperationTimeout";
	public static final String DELETE_ERROR_DEPENDENT_MSG = "DeleteErrorDependent";
	public static final String SAVE_ERROR_NOT_UNIQUE_MSG = "SaveErrorNotUnique";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DBException(int status, String msg) {
		super(status, msg);
	}
	

	public DBException(int status, String code, String msg) {
		super(status, code, msg);
	}


	private static final boolean isSQLState(Exception e, String SQLState) {
    	if (e == null) {
    		return false;
    	}
    	else if (e instanceof SQLException) {
    		return ((SQLException)e).getSQLState().equals(SQLState);
    	}
    	else if (e instanceof DBException) {
    		SQLException sqlEx = ((DBException)e).getSQLException();
    		if (sqlEx != null)
    			return sqlEx.getSQLState().equals(SQLState);
    		else
    			return false;
    	}
    	return false;
    }
	 /**
     * Check if Unique Constraint Exception (aka ORA-00001)
     * @param e exception
     */
    public static boolean isUniqueContraintError(Exception e) {
    	//if (RDBMS.isPostgreSQL())
    		return isSQLState(e, "23505");
    	//
    	//return isErrorCode(e, 1);
    }

    /**
     * Check if "child record found" exception (aka ORA-02292)
     * @param e exception
     */
    public static boolean isChildRecordFoundError(Exception e) {
    	//if (DB.isPostgreSQL())
    		return isSQLState(e, "23503");
    	//return isErrorCode(e, 2292);
    }

    /**
     * Check if "invalid identifier" exception (aka ORA-00904)
     * @param e exception
     */
    public static boolean isInvalidIdentifierError(Exception e) {
    	//if (DB.isPostgreSQL())
    		return isSQLState(e, "42P01");
    	//return isErrorCode(e, 904);
    }

    /**
     * Check if "invalid username/password" exception (aka ORA-01017)
     * @param e exception
     */
    public static boolean isInvalidUserPassError(Exception e) {
    	return isErrorCode(e, 1017);
    }

    /**
     * Check if "time out" exception (aka ORA-01013)
     * @param e
     */
    public static boolean isTimeout(Exception e) {
    	//if (DB.isPostgreSQL())
    		return isSQLState(e, "57014");
    	//return isErrorCode(e, 1013);
    }
    
	
    /**
     * @param e
     */
    public static String getDefaultDBExceptionMessage(Exception e) {
    	if (isUniqueContraintError(e)) {
    		return SAVE_ERROR_NOT_UNIQUE_MSG;
    	} else if (isChildRecordFoundError(e)) {
    		return DELETE_ERROR_DEPENDENT_MSG;
    	} else if (isTimeout(e)) {
    		return DATABASE_OPERATION_TIMEOUT_MSG;
    	} else {
    		return null;
    	}
    }
    
    private static final boolean isErrorCode(Exception e, int errorCode) {
    	if (e == null) {
    		return false;
    	}
    	else if (e instanceof SQLException) {
    		return ((SQLException)e).getErrorCode() == errorCode;
    	}
    	else if (e instanceof DBException) {
    		SQLException sqlEx = ((DBException)e).getSQLException();
    		if (sqlEx != null)
    			return sqlEx.getErrorCode() == errorCode;
    		else
    			return false;
    	}
    	return false;
    }
    
	/**
	 * @return Wrapped SQLException or null
	 */
	public SQLException getSQLException() {
		Throwable cause = getCause();
		if (cause instanceof SQLException)
			return (SQLException)cause;
		return null;
	}
}
