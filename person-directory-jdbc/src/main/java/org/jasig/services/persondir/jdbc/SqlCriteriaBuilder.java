package org.jasig.services.persondir.jdbc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jasig.services.persondir.criteria.BinaryLogicCriteria;
import org.jasig.services.persondir.criteria.CompareCriteria;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.criteria.IllegalCriteriaException;
import org.jasig.services.persondir.criteria.NotCriteria;
import org.jasig.services.persondir.criteria.CompareCriteria.CompareOperation;
import org.jasig.services.persondir.util.criteria.CriteriaHandler;
import org.jasig.services.persondir.util.criteria.CriteriaWalker;

final class SqlCriteriaBuilder implements CriteriaHandler {
    private final StringBuilder sb = new StringBuilder();
    private final List<Object> params = new ArrayList<Object>();
    private boolean negated = false;
    
    public String getSql() {
        return sb.toString();
    }
    
    public List<Object> getParams() {
        return params;
    }

    @Override
    public void handleBinaryLogicCriteria(BinaryLogicCriteria blc, CriteriaWalker walker) {
        sb.append("(");
        
        final List<Criteria> criteriaList = blc.getCriteriaList();
        for (final Iterator<Criteria> logicCriteriaItr = criteriaList.iterator(); logicCriteriaItr.hasNext();) {
            final Criteria logicCriteria = logicCriteriaItr.next();
            walker.walkCriteria(logicCriteria);
            
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

    @Override
    public void handleCompareCriteria(CompareCriteria cc, CriteriaWalker walker) {
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

    @Override
    public void handleNotCriteria(NotCriteria c, CriteriaWalker walker) {
        final boolean original = negated;
        negated = !negated;
        walker.walkCriteria(c.getCriteria());
        negated = original;
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