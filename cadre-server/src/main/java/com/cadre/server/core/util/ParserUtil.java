package com.cadre.server.core.util;

import java.util.Properties;

import javax.enterprise.inject.spi.CDI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cadre.server.core.entity.MVariable;
import com.cadre.server.core.persistence.exception.DBNoResultException;
import com.cadre.server.core.persistence.jdbc.RDBMS;
import com.cadre.server.core.service.ModelService;

public class ParserUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParserUtil.class);

    public static String getFilterCode(final Properties ctx,final String strExpression, final String trxName) {

       Evaluatee evaluatee = new Evaluatee() {
    	   
            public String get_ValueAsString(String variableName) {
                String value = ctx.getProperty(variableName);
                if (value == null) {
            		ModelService modelService =  CDI.current().select(ModelService.class).get();
            		try {
            			MVariable var = modelService.getPO(trxName, MVariable.TABLE_NAME, MVariable.COLUMNNAME_Value,variableName);
            			return ParserUtil.parseValue(ctx,var);
            			
            		}catch (DBNoResultException ex) {
        				LOGGER.error("getFilterCode(strExpression="+strExpression+")", ex);

            			return null;
            		}
                }
                else {
                    return value;
                }
            }
        };

        boolean ok = ParserUtil.isAllVariablesDefined(evaluatee, strExpression);
        if (!ok) {
            return "";
        }

        return ParserUtil.replaceAllVariablesDefined(evaluatee, strExpression);

    }

    public static String replaceAllVariablesDefined(Evaluatee source, String logic) {
        if (logic == null || logic.length() == 0)
            return logic;
        //
        boolean found = true;
        while (found) {
            int first = logic.indexOf('@');
            if (first == -1)
                break;
            int second = logic.indexOf('@', first + 1);
            if (second == -1)
                break;

            String variable = logic.substring(first + 1, second);
            String eval = source.get_ValueAsString(variable);

            if (eval == null || eval.length() == 0)
                break;
            //
            logic = logic.replace(logic.substring(first, second + 1), eval);

        }
        return logic;
	}

    public static boolean isAllVariablesDefined(Evaluatee source, String logic) {
        if (logic == null || logic.length() == 0)
            return true;
        //
        int pos = 0;
        while (pos < logic.length()) {
            int first = logic.indexOf('@', pos);
            if (first == -1)
                return true;
            int second = logic.indexOf('@', first + 1);
            if (second == -1) {
                return false;
            }
            String variable = logic.substring(first + 1, second);
            String eval = source.get_ValueAsString(variable);
            if (eval == null || eval.length() == 0)
                return false;
            //
            pos = second + 1;
        }
        return true;
    } // isAllVariablesDefined

	protected static String parseValue(Properties ctx,MVariable var) {
        if (var.isSQLValue()) {

            Evaluatee evaluatee = new Evaluatee() {

                public String get_ValueAsString(String variableName) {
                    return ctx.getProperty(variableName);
                }
            };

            boolean ok = ParserUtil.isAllVariablesDefined(evaluatee, var.getColumnSQL());
            if (!ok) {
                return "";
            }

            String sql = ParserUtil.replaceAllVariablesDefined(evaluatee, var.getColumnSQL());

            return RDBMS.getSQLValueStringEx(var.get_TrxName(), sql);

        }
        else if (var.isStaticValue()) {
            return var.getConstantValue();
        }else {
            return "";
        }
	}

}
