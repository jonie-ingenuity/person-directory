package org.jasig.services.persondir.jdbc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jasig.services.persondir.criteria.AndCriteria;
import org.jasig.services.persondir.criteria.BinaryLogicCriteria;
import org.jasig.services.persondir.criteria.CompareCriteria;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.criteria.EqualsCriteria;
import org.jasig.services.persondir.criteria.GreaterThanCriteria;
import org.jasig.services.persondir.criteria.GreaterThanOrEqualsCriteria;
import org.jasig.services.persondir.criteria.IllegalCriteriaException;
import org.jasig.services.persondir.criteria.LessThanCriteria;
import org.jasig.services.persondir.criteria.LessThanOrEqualsCriteria;
import org.jasig.services.persondir.criteria.LikeCriteria;
import org.jasig.services.persondir.criteria.NotCriteria;
import org.jasig.services.persondir.criteria.OrCriteria;
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
                    sb.append(getComparisonOperator(blc));
                }
                else {
                    sb.append(getComparisonOperator(blc.getNegatedForm()));
                }
                sb.append(" ");
            }
        }
        
        sb.append(")");
    }

    @Override
    public void handleCompareCriteria(CompareCriteria<?> cc, CriteriaWalker walker) {
        final String attribute = cc.getAttribute();
        final Object value = cc.getValue();

        sb.append(" ").append(attribute);
        
        //Append the operator and for non-null parameters add to the parameter map
        if (value != null) {
            appendOperator(cc, sb);
            params.add(value);
        }
        else {
            appendNullOperator(cc, sb);
        }
    }

    @Override
    public void handleNotCriteria(NotCriteria c, CriteriaWalker walker) {
        final boolean original = negated;
        negated = !negated;
        walker.walkCriteria(c.getCriteria());
        negated = original;
    }
    
    protected String getComparisonOperator(BinaryLogicCriteria criteria) {
        if (criteria instanceof AndCriteria) {
            return "AND";
        }
        else if (criteria instanceof OrCriteria) {
            return "OR";
        }
        else {
            throw new IllegalCriteriaException("Unsupported CompareOperation: " + criteria.getClass().getName());
        }
    }

    protected void appendOperator(CompareCriteria<?> criteria, StringBuilder sb) {
        if (criteria instanceof EqualsCriteria) {
            if (negated) {
                sb.append(" <> ?");
            }
            else {
                sb.append(" = ?");
            }
        }
        else if (criteria instanceof LikeCriteria) {
            if (negated) {
                sb.append(" NOT LIKE ?");
            }
            else {
                sb.append(" LIKE ?");
            }
        }
        else if (criteria instanceof GreaterThanCriteria) {
            if (negated) {
                sb.append(" <= ?");
            }
            else {
                sb.append(" > ?");
            }
        }
        else if (criteria instanceof GreaterThanOrEqualsCriteria) {
            if (negated) {
                sb.append(" < ?");
            }
            else {
                sb.append(" >= ?");
            }
        }
        else if (criteria instanceof LessThanCriteria) {
            if (negated) {
                sb.append(" >= ?");
            }
            else {
                sb.append(" < ?");
            }
        }
        else if (criteria instanceof LessThanOrEqualsCriteria) {
            if (negated) {
                sb.append(" > ?");
            }
            else {
                sb.append(" <= ?");
            }
        }
        else {
            throw new IllegalCriteriaException("Unsupported CompareOperation: " + criteria.getClass().getName());
        }
    }
    
    protected void appendNullOperator(CompareCriteria<?> criteria, StringBuilder sb) {
        if (criteria instanceof EqualsCriteria) {
            if (negated) {
                sb.append(" IS NOT NULL");
            }
            else {
                sb.append(" IS NULL");
            }
        }
        else {
            throw new IllegalCriteriaException("Cannot use " + criteria.getClass().getName() + " with a null value");
        }
    }
}