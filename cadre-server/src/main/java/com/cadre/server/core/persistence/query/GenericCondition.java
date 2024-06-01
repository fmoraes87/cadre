package com.cadre.server.core.persistence.query;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class GenericCondition {
	
	private LogicalOperator logicalOperator;
	private String parameterName;
	private ConditionOperator op;
	private List<?> values;

	private GenericCondition(ConditionOperator op) {
		this.logicalOperator=LogicalOperator.AND;
		this.op = op;
		
	}
	
	private GenericCondition(ConditionOperator op,LogicalOperator logicalOperator) {
		this.logicalOperator=logicalOperator;
		this.op = op;
		
	}

	public static GenericCondition equals(String parameterName, Object value) {
		GenericCondition condition = new GenericCondition(ConditionOperator.EQUAL);
		condition.setParameterName(parameterName);
		condition.setValues(Collections.singletonList(value));
		return condition;
	}

	public static GenericCondition in(String parameterName, Object... values) {
		GenericCondition condition = new GenericCondition(ConditionOperator.IN);
		condition.setParameterName(parameterName);
		condition.setValues(Arrays.asList(values));
		return condition;
	}
	
	public static GenericCondition ge(String parameterName, Object value) {
		GenericCondition condition = new GenericCondition(ConditionOperator.GREATER_OR_EQUAL);
		condition.setParameterName(parameterName);
		condition.setValues(Collections.singletonList(value));
		return condition;
	}
	
	public static GenericCondition gt(String parameterName, Object value) {
		GenericCondition condition = new GenericCondition(ConditionOperator.GREATER);
		condition.setParameterName(parameterName);
		condition.setValues(Collections.singletonList(value));
		return condition;
	}
	
	public static GenericCondition le(String parameterName, Object value) {
		GenericCondition condition = new GenericCondition(ConditionOperator.LESS_OR_EQUAL);
		condition.setParameterName(parameterName);
		condition.setValues(Collections.singletonList(value));
		return condition;
	}
	
	
	public ConditionOperator getConditionOperator() {
		return op;
	}

	public String getParameterName() {
		return parameterName;
	}	

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public List<?> getValues() {
		return values;
	}

	public void setValues(List<?> values) {
		this.values = values;
	}

	public LogicalOperator getLogicalOperator() {
		return logicalOperator;
	}
	
	
	private String convertOperatorToSQLString(final ConditionOperator op) {
		switch (op) {
		case EQUAL:
			return JDBCSearchQuery.STR_EQUAL;
		case IN:
			return JDBCSearchQuery.STR_IN;
		case GREATER_OR_EQUAL:
			return JDBCSearchQuery.STR_GE;
		case LESS_OR_EQUAL:
			return JDBCSearchQuery.STR_LE;			
		case GREATER:
			return JDBCSearchQuery.STR_GT;	
		default:
			break;
		}
		return null;
	}
	
	public String prepareSQL(String tableName) {
		StringBuilder sql = new StringBuilder();
		sql.append(tableName);
		sql.append(JDBCSearchQuery.STR_DOT_SEPARATOR);
		sql.append(this.parameterName);
		if (CollectionUtils.isNotEmpty(this.values)) {
			int size = this.values.size();
			sql.append(StringUtils.SPACE);
			sql.append(convertOperatorToSQLString(this.op));
			sql.append(StringUtils.SPACE);
			sql.append("(");
			sql.append(StringUtils.join(Collections.nCopies(size, "?"), JDBCSearchQuery.STR_DEFAULT_SEPARATOR));
			sql.append(")");
		} else {
			sql.append(StringUtils.SPACE);
			sql.append("IS NULL");
			sql.append(StringUtils.SPACE);
		}
		
		return sql.toString();
	}


}
