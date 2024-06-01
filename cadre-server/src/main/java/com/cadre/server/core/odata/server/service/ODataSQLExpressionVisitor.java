package com.cadre.server.core.odata.server.service;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.commons.core.edm.primitivetype.EdmBoolean;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDate;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDateTimeOffset;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitor;
import org.apache.olingo.server.api.uri.queryoption.expression.Literal;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.api.uri.queryoption.expression.UnaryOperatorKind;

import com.cadre.server.core.CadreEnv;
import com.cadre.server.core.entity.MTable;
import com.cadre.server.core.entity.POInfo;

public class ODataSQLExpressionVisitor implements ExpressionVisitor<String> {

    private POInfo p_info;

    public ODataSQLExpressionVisitor(POInfo p_info) {
        this.p_info = p_info;
    }

    @Override
    public String visitBinaryOperator(final BinaryOperatorKind operator, final String left, final String right)
            throws ExpressionVisitException, ODataApplicationException {

        String sqlOperator = null;

        switch (operator) {
            case AND:
                sqlOperator = "AND";
                break;
            case OR:
                sqlOperator = "OR";
                break;
            case EQ:
                sqlOperator = "=";
                break;
            case NE:
                sqlOperator = "<>";
                break;
            case GE:
                sqlOperator = ">=";
                break;
            case GT:
                sqlOperator = ">";
                break;
            case LE:
                sqlOperator = "<=";
                break;
            case LT:
                sqlOperator = "<";
                break;
            case ADD:
                sqlOperator = "+";
                break;
            case SUB:
                sqlOperator = "-";
                break;
            case MUL:
                sqlOperator = "*";
                break;
            case DIV:
                sqlOperator = "/";
                break;
            case MOD:
                sqlOperator = "%";
                break;
            case HAS:
                return throwNotImplemented();

            default:
                return throwNotImplemented();
        }

        // return the binary statement
        return "(" + left + StringUtils.SPACE + sqlOperator + StringUtils.SPACE + right + ")";
    }

    @Override
    public String visitUnaryOperator(final UnaryOperatorKind operator, final String operand)
            throws ExpressionVisitException, ODataApplicationException {

        switch (operator) {
            case MINUS:
                return "-" + operand;
            case NOT:
                return "NOT " + operand;
            default:
                // Can't happen.
                return throwNotImplemented();
        }
    }

    @Override
    public String visitLiteral(final Literal literal) throws ExpressionVisitException, ODataApplicationException {
        if (literal.getType() instanceof EdmPrimitiveType) {
            if (literal.getType() instanceof EdmBoolean) {
                return new Boolean(literal.getText()).booleanValue() ? " 'Y' " : "'N'";
            }
            else if (literal.getType() instanceof EdmDateTimeOffset) {
                return "'" + literal.getText().replace("T", " ").replace("Z", "") + "'";
            }
            else {
                // TODO- Prevent sql injection
                return literal.getText();
            }
        }
        else if (literal.getType() instanceof EdmDate) {
            return "'" + literal.getText().replace("T", " ") + "'";
        }
        else {
            return "'" + literal.getText() + "'";
        }
    }

    @Override
    public String visitAlias(String arg0) throws ExpressionVisitException, ODataApplicationException {
        return throwNotImplemented();

    }

    @Override
    public String visitEnum(final EdmEnumType type, final List<String> enumValues) throws ExpressionVisitException,
            ODataApplicationException {
        return throwNotImplemented();

    }

    @Override
    public String visitLambdaExpression(String arg0, String arg1, Expression arg2) throws ExpressionVisitException,
            ODataApplicationException {
        return throwNotImplemented();
    }

    @Override
    public String visitLambdaReference(final String variableName) throws ExpressionVisitException,
            ODataApplicationException {
        return throwNotImplemented();
    }

    @Override
    public String visitMember(final Member member) throws ExpressionVisitException, ODataApplicationException {

        final List<UriResource> uriResourceParts = member.getResourcePath().getUriResourceParts();

        // UriResourceParts contains at least one UriResource.
        final UriResource initialPart = uriResourceParts.get(0);
        if (initialPart instanceof UriResourceProperty) {
            EdmProperty currentEdmProperty = ((UriResourceProperty) initialPart).getProperty();
            // Check if NOT base language and column is translated => load trl from db
            if (!CadreEnv.isBaseLanguageSelected()
                    && p_info.getPOInfoColumn(currentEdmProperty.getName()).isTranslatable) {
                return p_info.getTableName() + MTable.TRL_PREFIX + "." + currentEdmProperty.getName();
            }
            else {
                return p_info.getTableName() + "." + currentEdmProperty.getName();
            }
        }
        else if (initialPart instanceof UriResourceFunction) {
            return throwNotImplemented();
        }
        else {
            return throwNotImplemented();
        }

    }

    @Override
    public String visitMethodCall(final MethodKind methodCall, final List<String> parameters)
            throws ExpressionVisitException, ODataApplicationException {

        StringBuilder sb = new StringBuilder();

        if (parameters.size() == 0) {
            switch (methodCall) {
                case NOW:
                    sb.append("NOW()");
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported method: " + methodCall.name());

            }
        }
        // One parameter methods
        else if (parameters.size() == 1) {
            switch (methodCall) {
                case CEILING:
                    sb.append("CEIL(");
                    break;
                case DAY:
                    sb.append("EXTRACT(DAY FROM ");
                    break;
                case FLOOR:
                    sb.append("FLOOR(");
                    break;
                case HOUR:
                    sb.append("EXTRACT(HOUR FROM TIMESTAMP ");
                    break;
                case LENGTH:
                    sb.append("LENGTH(");
                    break;
                case MINUTE:
                    sb.append("EXTRACT(MINUTE FROM TIMESTAMP ");
                    break;
                case MONTH:
                    sb.append("EXTRACT(MOUNTH FROM TIMESTAMP ");
                    break;
                case ROUND:
                    sb.append("ROUND(");
                    break;
                case SECOND:
                    sb.append("EXTRACT(SECOND FROM TIMESTAMP ");
                    break;
                case TOUPPER:
                    sb.append("UPPER(");
                    break;
                case TOLOWER:
                    sb.append("LOWER(");
                    break;
                case TRIM:
                    sb.append("TRIM(both ' ' from   ");
                    break;
                case YEAR: // Postgres
                    sb.append("EXTRACT(YEAR FROM ");
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported method: " + methodCall.name());
            }
            sb.append(parameters.get(0));
            sb.append(')');
        }
        // Other methods
        else if (methodCall == MethodKind.INDEXOF || methodCall == MethodKind.CONCAT
                || methodCall == MethodKind.SUBSTRING) {
            switch (methodCall) {
                case INDEXOF:// Postgres
                    sb.append("POSITION(");
                    sb.append(parameters.get(1));
                    sb.append(" IN ");
                    sb.append(parameters.get(0));
                    sb.append(')');
                    break;
                case CONCAT:
                    sb.append("CONCAT(");
                    sb.append(parameters.get(0));
                    sb.append(',');
                    sb.append(parameters.get(1));
                    sb.append(')');
                    break;
                case SUBSTRING:
                    sb.append("SUBSTR(");
                    sb.append(parameters.get(0));
                    sb.append(',');
                    sb.append(parameters.get(1));
                    sb.append(')');
                    break;
                default:
            }

        }

        else {
            switch (methodCall) {
                case ENDSWITH:
                    sb.append(parameters.get(0));
                    sb.append(" LIKE '%");
                    sb.append(removeApostrophes(parameters.get(1)));
                    sb.append("'");
                    break;
                case STARTSWITH:
                    sb.append(parameters.get(0));
                    sb.append(" LIKE '");
                    sb.append(removeApostrophes(parameters.get(1)));
                    sb.append("%'");
                    break;
                case CONTAINS:
                    sb.append("LOWER("+removeApostrophes(parameters.get(0))+")");
                    sb.append(" LIKE LOWER('%");
                    sb.append(removeApostrophes(parameters.get(1)));
                    sb.append("%')");
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported method: " + methodCall.name());
            }
        }

        return sb.toString();
    }

    @Override
    public String visitTypeLiteral(final EdmType type) throws ExpressionVisitException, ODataApplicationException {
        return throwNotImplemented();
    }

    private String throwNotImplemented() throws ODataApplicationException {
        throw new ODataApplicationException("Not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
                Locale.ROOT);
    }

    /** Removes the first and last character if they are apostrophes. */
    private static String removeApostrophes(Object o) {
        String s = String.class.cast(o);
        if (s.charAt(0) == '\'' && s.charAt(s.length() - 1) == '\'')
            return s.substring(1, s.length() - 1);
        return s;
    }
}
