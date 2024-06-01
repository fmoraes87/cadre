package com.cadre.server.core.persistence.query;

import java.sql.PreparedStatement;

import org.apache.commons.lang3.StringUtils;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.entity.MTable;
import com.cadre.server.core.entity.POInfo;
import com.cadre.server.core.entity.POModel;

public interface JDBCSearchQuery extends SearchQuery {
	
	public static final String STR_SELECT = "SELECT";
	public static final String STR_WHERE = "WHERE";
	public static final String STR_FROM = "FROM";

	public static final String STR_ORDER_BY = "ORDER BY";
	public static final String STR_DEFAULT_SEPARATOR = ",";
	public static final String STR_DOT_SEPARATOR = ".";
	public static final String STR_AND = "AND";
	public static final String STR_EQUAL = "=";
	public static final String STR_LEFT_OUTER_JOIN = "LEFT OUTER JOIN";
	public static final String STR_ON = "ON";
	public static final String STR_IN = "IN";
	public static final String STR_GE = ">=";
	public static final String STR_LE = "<=";
	public static final String STR_GT = ">";
	public static final String STR_LT = "<";



	public PreparedStatement getCPreparedStatement(String trxName, POInfo poInfo);

	default  String getFromClause(boolean translated) {
		StringBuilder sql = new StringBuilder(STR_FROM);
		sql.append(StringUtils.SPACE);
		sql.append(getTableName());
		sql.append(StringUtils.SPACE);
		if (translated && !CadreEnv.isBaseLanguageSelected()) {
			sql.append(STR_LEFT_OUTER_JOIN).append(StringUtils.SPACE).append(getTableNameTrl())
				.append(StringUtils.SPACE).append(STR_ON).append(StringUtils.SPACE).append(getTableName()).append(STR_DOT_SEPARATOR).append(getTableName()+"_ID")
				.append(StringUtils.SPACE).append(STR_EQUAL).append(StringUtils.SPACE).append(getTableNameTrl()).append(STR_DOT_SEPARATOR).append(getTableName()+"_ID")	
				.append(StringUtils.SPACE).append(STR_AND).append(StringUtils.SPACE).append(getTableNameTrl()).append(STR_DOT_SEPARATOR).append(POModel.COLUMNNAME_LANGUAGE)
				.append(StringUtils.SPACE).append(STR_EQUAL).append(StringUtils.SPACE).append("'").append(CadreEnv.getAD_Language()).append("'");	
		}
		
		return sql.toString();
		
	}
	
	default String getTableNameTrl() {
		return getTableName()+MTable.TRL_PREFIX;
	}

	
}
