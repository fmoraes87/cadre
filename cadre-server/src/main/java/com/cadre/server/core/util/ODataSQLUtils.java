package com.cadre.server.core.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.entity.MTable;
import com.cadre.server.core.entity.POInfo;
import com.cadre.server.core.odata.server.service.ODataSQLExpressionVisitor;

public class ODataSQLUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ODataSQLUtils.class);

	
	public static String getWhereClause(POInfo poInfo, FilterOption filterOption) {
		if (null!=filterOption && null!=filterOption.getExpression()) {
			Expression ex = filterOption.getExpression();
			StringBuilder whereClause = new StringBuilder("1=1" + StringUtils.SPACE);
			try {
				whereClause.append(" AND " + ex.accept(new ODataSQLExpressionVisitor(poInfo)));

			} catch (ExpressionVisitException | ODataApplicationException e1) {
				LOGGER.error("getWhereClause(poInfo="+poInfo+"expression="+ex +")", e1);
	
				whereClause.append(" 1=2 ");
			}
			
			return  whereClause.toString();
		}
		
		return " 1=1 ";

	}

	public static String getOrderBy(POInfo poInfo, OrderByOption orderByOption) {
		if (orderByOption != null) {
			StringBuffer orderBy = new StringBuffer();
			List<OrderByItem> orderItemList = orderByOption.getOrders();
			int count = 0;
			for (OrderByItem orderByItem : orderItemList) {
				Expression expression = orderByItem.getExpression();
				if (expression instanceof Member) {
					UriInfoResource resourcePath = ((Member) expression).getResourcePath();
					UriResource resource = resourcePath.getUriResourceParts().get(0);
					if (resource instanceof UriResourcePrimitiveProperty) {
						EdmProperty edmProperty = ((UriResourcePrimitiveProperty) resource).getProperty();
						final String sortPropertyName = edmProperty.getName();
						if (count > 0) {
							orderBy.append(",");
						} else if (count == 0) {
							orderBy.append(" ORDER BY ");
						}
						orderBy.append(getColumnWithPrefixo(poInfo, sortPropertyName));
						orderBy.append(orderByItem.isDescending() ? " DESC " : " ASC ");
						count++;
					}
				}
			}
			return orderBy.toString();
		}
		return StringUtils.EMPTY;

		
	}
	
    public static String getSkip(SkipOption skipOption) {
        // handle $skip
        StringBuilder skip = new StringBuilder();

        if (skipOption != null) {
            int skipNumber = skipOption.getValue();
            if (skipNumber >= 0) {
                skip.append( StringUtils.SPACE + "OFFSET" +  StringUtils.SPACE + skipNumber);
            }
            else {
                throw new IllegalStateException("Invalid value for $skip");
            }
        }

        return skip.toString();
    }

    public static String getTop(TopOption topOption) {
        // handle $top
        StringBuilder top = new StringBuilder();
        if (topOption != null) {
            int topNumber = topOption.getValue();
            if (topNumber >= 0) {
                top.append("LIMIT" +  StringUtils.SPACE + topNumber +  StringUtils.SPACE);
            }
            else {
                throw new IllegalStateException("Invalid value for $top");
            }
        }

        return top.toString();

    }
	

    private static String getColumnWithPrefixo(POInfo poInfo, String columnName) {

        // Check if NOT base language and column is translated => load trl from db
        if (!CadreEnv.isBaseLanguageSelected()
                && poInfo.getPOInfoColumn(columnName).isTranslatable) {       
            return poInfo.getTableName() + MTable.TRL_PREFIX + "." + columnName;
        }
        else {
            return poInfo.getTableName() + "." + columnName;

        }
    }

}
