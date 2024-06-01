package com.cadre.server.core.persistence.query;

import java.sql.PreparedStatement;

import org.apache.commons.lang3.StringUtils;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.entity.POInfo;
import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.persistence.jdbc.StatementFactory;

public class FlexibleSearchQuery implements JDBCSearchQuery {

	private String tableName;
	private String whereClause;
	private String orderBy;
	private String topSQL;
	private String skipSQL;

	public FlexibleSearchQuery(String tableName) {
		this.tableName = tableName;
	}
	
	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public String getTableName() {
		return tableName;
	}


	@Override
	public PreparedStatement getCPreparedStatement(String trxName, POInfo poInfo) {
		final StringBuilder sql = new StringBuilder(STR_SELECT);
		sql.append(StringUtils.SPACE);
		sql.append(StringUtils.join(poInfo.getColumnsName(), STR_DEFAULT_SEPARATOR));
		sql.append(StringUtils.SPACE);
		sql.append(this.getFromClause(poInfo.isTranslated()));
		sql.append(StringUtils.SPACE);
		//  Do we have to add WHERE or AND
		if (StringUtils.isNotEmpty(this.whereClause)) {
			sql.append("WHERE" + StringUtils.SPACE+ this.whereClause);
			sql.append(StringUtils.SPACE);
			sql.append(STR_AND);
		}else {
			sql.append(STR_WHERE);
		}
		sql.append(StringUtils.SPACE);
		sql.append(this.tableName).append(STR_DOT_SEPARATOR);
		sql.append(POModel.COLUMNNAME_AD_Client_ID + StringUtils.SPACE+ "IN (0," + CadreEnv.getAD_Client_ID() + ")");
		sql.append(StringUtils.SPACE);
		sql.append(STR_AND);
		sql.append(StringUtils.SPACE);
		sql.append(this.tableName).append(STR_DOT_SEPARATOR);
		sql.append(POModel.COLUMNNAME_AD_Org_ID + StringUtils.SPACE+ "IN (0," + CadreEnv.getAD_Org_ID() + ")");
		
		 if (CadreEnv.isViewOnlyActiveRecords()) {
			 sql.append(StringUtils.SPACE);
			 sql.append(STR_AND);
			 sql.append(StringUtils.SPACE);
			 sql.append(this.tableName).append(STR_DOT_SEPARATOR);
			 sql.append(POModel.COLUMNNAME_IsActive + "='" + POModel.YES_VALUE+"'");
		 }
		
		sql.append(StringUtils.SPACE);
		if (StringUtils.isNotEmpty(this.orderBy)) {
			sql.append(this.orderBy);			
		}
		if (StringUtils.isNotEmpty(this.topSQL)) {
			sql.append(this.topSQL);			
		}
		if (StringUtils.isNotEmpty(this.skipSQL)) {
			sql.append(this.skipSQL);			
		}
		
		return StatementFactory.newCPreparedStatement(trxName, sql.toString());
	}
	
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		
	}
	
	public void setTop(String topSQL) {
		this.topSQL = topSQL;
		
	}
	
	public void setSkip(String skipSQL) {
		this.skipSQL = skipSQL;
		
	}



}
