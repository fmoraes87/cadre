package com.cadre.server.core.persistence.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.entity.POInfo;
import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.persistence.exception.DBException;
import com.cadre.server.core.persistence.jdbc.JDBCUtilities;
import com.cadre.server.core.persistence.jdbc.StatementFactory;

public class JDBCQueryImpl implements JDBCSearchQuery {

	private static final Logger LOGGER = LoggerFactory.getLogger(JDBCQueryImpl.class);


	/** Table Name */
	private String tableName = "";
	
	private List<GenericCondition> conditionList;
	
	private List<GenericSearchOrderBy> orderByList;
	
	private Integer top;
	
	private Integer skip;

	public String orderBy;

	private JDBCQueryImpl() {
		
	}

	public String getTableName() {
		return tableName;
	}
	
	public List<GenericCondition> getConditionList() {
		return conditionList;
	}

	public void setConditionList(List<GenericCondition> conditionList) {
		this.conditionList = conditionList;
	}
	
	private String createSQLFrom(POInfo poInfo) {

		final StringBuilder sql = new StringBuilder(STR_SELECT);
		sql.append(StringUtils.SPACE);
		sql.append(StringUtils.join(poInfo.getColumnsName(), STR_DEFAULT_SEPARATOR));
		sql.append(StringUtils.SPACE);
		sql.append(this.getFromClause(poInfo.isTranslated()));
		sql.append(StringUtils.SPACE);
		sql.append(this.getWhereClause(poInfo));
		sql.append(StringUtils.SPACE);
		sql.append(this.getOrderBy());
		sql.append(StringUtils.SPACE);
		sql.append(this.getTopSQL());
		sql.append(StringUtils.SPACE);
		sql.append(this.getSkipSQL());

		return sql.toString().trim();
	}

	private String getSkipSQL() {
        if (skip > 0) {
            return StringUtils.SPACE + "OFFSET" +  StringUtils.SPACE + skip;
        }
        else {
            return StringUtils.EMPTY;
        }
	}

	private String getTopSQL() {
        if (top > 0) {
            return "LIMIT" +  StringUtils.SPACE + top +  StringUtils.SPACE;
        }
        else {
            return StringUtils.EMPTY;
        }
	}

	private String getWhereClause(POInfo poInfo) {
		this.conditionList.add(GenericCondition.in(POModel.COLUMNNAME_AD_Client_ID,ConditionOperator.IN,POModel.ID_ZERO,CadreEnv.getAD_Client_ID()));
		this.conditionList.add(GenericCondition.in(POModel.COLUMNNAME_AD_Org_ID,ConditionOperator.IN,POModel.ID_ZERO,CadreEnv.getAD_Org_ID()));
		
		final StringBuilder sql = new StringBuilder();
		sql.append(StringUtils.SPACE);
		sql.append(STR_WHERE);
		sql.append(StringUtils.SPACE);
		sql.append("1=1");
		sql.append(StringUtils.SPACE);
		this.getConditionList().forEach(condition -> {
			sql.append(condition.getLogicalOperator());
			getSingleCondition(poInfo, sql, condition);
		});

		return sql.toString();
	}

	private void getSingleCondition(POInfo poInfo, final StringBuilder sql, GenericCondition condition) {
		sql.append(StringUtils.SPACE);
		if (poInfo.isTranslated() && !CadreEnv.isBaseLanguageSelected()
				&& !condition.getParameterName().equals(POModel.COLUMNNAME_LANGUAGE)
				&& poInfo.getPOInfoColumn(condition.getParameterName()).isTranslatable) {
			sql.append(condition.prepareSQL(this.getTableNameTrl()));

		} else {
			sql.append(condition.prepareSQL(this.getTableName()));
		}
		
		
	}
	
	private String getOrderBy() {
		if (CollectionUtils.isNotEmpty(this.orderByList)) {
			final StringBuilder sql = new StringBuilder();
			sql.append(StringUtils.SPACE);
			sql.append(STR_ORDER_BY);
			sql.append(StringUtils.SPACE);
			sql.append(StringUtils.join(orderByList,STR_DEFAULT_SEPARATOR));
			
			return sql.toString();
			
		}else {
			return StringUtils.EMPTY;
		}
	}
	
	


	@Override
	public PreparedStatement getCPreparedStatement(String trxName, POInfo poInfo) {
		
		String sql = createSQLFrom(poInfo);
		PreparedStatement pstmt = StatementFactory.newCPreparedStatement(trxName, sql.toString());
		try {
			if (CollectionUtils.isNotEmpty(this.getConditionList())) {
				
				int index = 1 ; 
				for (GenericCondition condition: this.getConditionList()) {
					if (CollectionUtils.isNotEmpty(condition.getValues())) {
						for (Object paramValue: condition.getValues()) {
							JDBCUtilities.configPreparedStatement(pstmt, poInfo, index, condition.getParameterName(),
									paramValue);
							index++;
							
						}
					}
				}
			}
			
		}catch (SQLException e) {
			LOGGER.error("getCPreparedStatement("+ poInfo+")", e);
			throw new DBException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
		}
		
		return pstmt;
	}

	
	public static class Builder {
		private static GenericCondition onlyActiveRecordCondition = GenericCondition.equals(POModel.COLUMNNAME_IsActive,true);

		//private static GenericCondition laguageCondition = new GenericCondition(POModel.COLUMNNAME_LANGUAGE,ConditionOperator.EQUAL,CadreEnv.getAD_Language());

		/** Table Name */
		private String tableName = "";
		//private boolean onlyActiveRecords= false;
		private int top;
		private int skip;
		
		private List<GenericCondition> conditionList = new LinkedList<>();
		private List<GenericSearchOrderBy> orderByList = new LinkedList<>();
		
		public Builder(String tableName) {
			this.tableName = tableName;
		}
		
		private Builder addCondition(GenericCondition condition) {
			if (condition !=null) {
				conditionList.add(condition);
			}
			
			return this;
			
		}
		
		 public JDBCQueryImpl build(){
			 JDBCQueryImpl query = new JDBCQueryImpl();
			 query.tableName = tableName;
			 
			 if (CadreEnv.isViewOnlyActiveRecords()) {
				 addCondition(onlyActiveRecordCondition);
			 }

			 query.conditionList = conditionList;
			 query.orderByList = orderByList;
			 query.top=this.top;
			 query.skip=this.skip;
			 
			 return query;
		 }

		public Builder and(GenericCondition genericCondition) {
			addCondition(genericCondition);
			return this;
		}
		
		public Builder addOrderBy(GenericSearchOrderBy orderBy) {
			this.orderByList.add(orderBy);
			return this;
		}

		public Builder top(int i) {
			this.top=i;
			return this;

		}
		
		public Builder skip(int i) {
			this.top=i;
			return this;

		}
	}




}
