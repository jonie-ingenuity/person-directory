package org.jasig.services.persondir.jdbc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.criteria.BinaryLogicCriteria;
import org.jasig.services.persondir.criteria.CompareCriteria;
import org.jasig.services.persondir.criteria.CompareCriteria.CompareOperation;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.criteria.IllegalCriteriaException;
import org.jasig.services.persondir.criteria.NotCriteria;
import org.jasig.services.persondir.spi.CriteriaSearchableAttributeSource;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * @author Eric Dalquist
 */
public class CriteriaJdbcPersonSource implements CriteriaSearchableAttributeSource {
    public static final String DEFAULT_CRITERIA_PLACEHOLDER_PATTERN = "{}";
    
    private JdbcOperations jdbcOperations;
    private String queryTemplate;
    private ResultSetExtractor<List<PersonAttributes>> resultSetExtractor;
    private Pattern criteriaPlaceholderPattern = Pattern.compile(Pattern.quote(DEFAULT_CRITERIA_PLACEHOLDER_PATTERN));
    
    public void setCriteriaPlaceholder(String criteriaPlaceholderPattern) {
        this.criteriaPlaceholderPattern = Pattern.compile(Pattern.quote(criteriaPlaceholderPattern));
    }
    public void setCriteriaPlaceholderPattern(String criteriaPlaceholderPattern) {
        this.criteriaPlaceholderPattern = Pattern.compile(criteriaPlaceholderPattern);
    }

    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public void setQueryTemplate(String queryTemplate) {
        this.queryTemplate = queryTemplate;
    }
    
    public void setResultSetExtractor(
            ResultSetExtractor<List<PersonAttributes>> resultSetExtractor) {
        this.resultSetExtractor = resultSetExtractor;
    }

    @Override
    public List<PersonAttributes> searchForAttributes(Criteria criteria) {
        final LinkedList<Object> params = new LinkedList<Object>();
        final String sqlCriteria = this.generateSqlCriteria(criteria, params);
        
        final Matcher queryTemplateMatcher = criteriaPlaceholderPattern.matcher(this.queryTemplate);
        final String query = queryTemplateMatcher.replaceAll(sqlCriteria);
        
        return this.jdbcOperations.query(query, params.toArray(), this.resultSetExtractor);
    }
    
    protected String generateSqlCriteria(Criteria criteria, List<Object> params) {
        final StringBuilder sb = new StringBuilder();
        
        generateSqlCriteria(criteria, sb, false, params);
        
        return sb.toString();
    }

    protected void generateSqlCriteria(Criteria criteria, StringBuilder sb, boolean negated, List<Object> params) {
        if (criteria instanceof BinaryLogicCriteria) {
            sb.append("(");
            
            final BinaryLogicCriteria blc = (BinaryLogicCriteria) criteria;
            final List<Criteria> criteriaList = blc.getCriteriaList();
            for (final Iterator<Criteria> logicCriteriaItr = criteriaList.iterator(); logicCriteriaItr.hasNext();) {
                final Criteria logicCriteria = logicCriteriaItr.next();
                generateSqlCriteria(logicCriteria, sb, negated, params);
                
                if (logicCriteriaItr.hasNext()) {
                    sb.append(" ");
                    if (negated) {
                        sb.append(blc.getOperation());
                    }
                    else {
                        sb.append(blc.getOperation().getNegatedForm());
                    }
                    sb.append(" ");
                }
            }
            
            sb.append(")");
        }
        else if (criteria instanceof NotCriteria) {
            generateSqlCriteria(((NotCriteria) criteria).getCriteria(), sb, !negated, params);
        }
        else if (criteria instanceof CompareCriteria) {
            final CompareCriteria cc = (CompareCriteria) criteria;
            final String attribute = cc.getAttribute();
            final Object value = cc.getValue();

            sb.append(" ").append(attribute);

            //Determine the comparison operator
            final CompareOperation operation;
            if (negated) {
                operation = cc.getOperation().getNegatedForm();
            }
            else {
                operation = cc.getOperation();
            }
            
            //Append the operator and for non-null parameters add to the parameter map
            if (value != null) {
                appendOperator(operation, sb);
                params.add(value);
            }
            else {
                appendNullOperator(operation, sb);
            }
        }
        else {
            throw new IllegalCriteriaException("Unsupported Criteria: " + criteria);
        }
    }

    protected void appendOperator(CompareOperation operation, StringBuilder sb) {
        switch (operation) {
            case EQUALS: {
                sb.append(" = ?");                    
                break;
            }
            case NOT_EQUALS: {
                sb.append(" <> ?");
                break;
            }
            case LIKE: {
                sb.append(" LIKE ?");
                break;
            }
            case NOT_LIKE: {
                sb.append(" NOT LIKE ?");
                break;
            }
            case GREATER_THAN: {
                sb.append(" > ?");
                break;
            }
            case GREATER_THAN_OR_EQUALS: {
                sb.append(" >= ?");
                break;
            }
            case LESS_THAN: {
                sb.append(" < ?");
                break;
            }
            case LESS_THAN_OR_EQUALS: {
                sb.append(" <= ?");
                break;
            }
            default: {
                throw new IllegalCriteriaException("Unsupported CompareOperation: " + operation);
            }
        }
    }
    
    protected void appendNullOperator(CompareOperation operation, StringBuilder sb) {
        switch (operation) {
            case EQUALS: {
                sb.append(" IS NULL");                    
                break;
            }
            case NOT_EQUALS: {
                sb.append(" IS NOT NULL");
                break;
            }
            default: {
                throw new IllegalCriteriaException("Cannot use " + operation + " with a null value");
            }
        }
    }
}
