package com.cadre.server.core.persistence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.persistence.exception.DBException;

public class StatementFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(StatementFactory.class);

	public static PreparedStatement newCPreparedStatement(String trxName, String sql) {

			try {
				Connection conn = null;
				Trx trx =trxName == null ? null : Trx.get(trxName, false);
				if (trx != null) {
					conn = trx.getConnection();
				}else {
					LOGGER.error("DBNoConnection");
					throw new DBException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),"@DBNoConnection@","No Connection");				
				}
				
				if (conn==null) {
					LOGGER.error("No Connection");
					throw new DBException(Status.INTERNAL_SERVER_ERROR.getStatusCode(),"@DBNoConnection@","No Connection");				
				}
				return conn.prepareStatement(sql);
			} catch (SQLException e) {
				LOGGER.error("newCPreparedStatement(" +sql +")", e);
				throw new DBException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
			}

		
	}


}
