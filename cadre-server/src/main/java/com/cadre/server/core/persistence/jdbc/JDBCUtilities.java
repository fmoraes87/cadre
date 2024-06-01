package com.cadre.server.core.persistence.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.beanutils.ConvertUtils;

import com.cadre.server.core.entity.POInfo;

public class JDBCUtilities {


	/**
	 * 
	 * @param pstmt
	 * @param poInfo
	 * @param currentPos
	 * @param paramName
	 * @param currentValue
	 * @throws SQLException
	 */
	public static void configPreparedStatement(final PreparedStatement pstmt, POInfo poInfo, final int currentPos,
			final String paramName, final Object currentValue) throws SQLException {
		
		if (currentValue!=null) {
			int columIndex = poInfo.getColumnIndex(paramName);
			Class<?> clazz = poInfo.getColumnClass(columIndex);
			Object value;
			if (clazz != currentValue.getClass()) {
				value = ConvertUtils.convert(currentValue, clazz);
			}else {
				value = currentValue;
			}
			RDBMS.setParameter(pstmt, currentPos, value);
			
		}else {
			RDBMS.setParameter(pstmt, currentPos, currentValue);
		}
		
	}
	
}
