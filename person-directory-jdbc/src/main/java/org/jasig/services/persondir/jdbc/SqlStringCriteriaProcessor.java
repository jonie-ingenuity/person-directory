package org.jasig.services.persondir.jdbc;

import java.util.ArrayList;
import java.util.List;

import org.jasig.services.persondir.criteria.AndCriteria;
import org.jasig.services.persondir.criteria.BaseCriteriaProcessor;
import org.jasig.services.persondir.criteria.BinaryLogicCriteria;
import org.jasig.services.persondir.criteria.OrCriteria;

public class SqlStringCriteriaProcessor extends BaseCriteriaProcessor {
    private final StringBuilder sb = new StringBuilder();
    private final List<Object> params = new ArrayList<Object>();
    
    public String toString() {
        return sb.toString();
    }
    
    public String getSql() {
        return this.toString();
    }
    
    public List<Object> getParams() {
        return params;
    }
    
    
    @Override
    public void appendBinaryLogicStart(BinaryLogicCriteria criteria) {
        sb.append("(");
    }

    @Override
    public void appendAndSeperator(AndCriteria criteria) {
        if (isNegated()) {
            sb.append(" OR ");
        }
        else {
            sb.append(" AND ");
        }
    }

    @Override
    public void appendOrSeperator(OrCriteria criteria) {
        if (isNegated()) {
            sb.append(" AND ");
        }
        else {
            sb.append(" OR ");
        }
    }

    @Override
    public void appendBinaryLogicEnd(BinaryLogicCriteria criteria) {
        sb.append(")");
    }

    @Override
    public void appendEquals(String name, Object value) {
        sb.append(name);
        if (value != null) {
            params.add(value);
            if (isNegated()) {
                sb.append(" <> ?");
            }
            else {
                sb.append(" = ?");
            }
        }
        else {
            if (isNegated()) {
                sb.append(" IS NOT NULL");
            }
            else {
                sb.append(" IS NULL");
            }
        }
    }

    @Override
    public void appendLike(String name, Object value) {
        sb.append(name);
        params.add(value);
        if (isNegated()) {
            sb.append(" NOT LIKE ?");
        }
        else {
            sb.append(" LIKE ?");
        }
    }

    @Override
    public void appendGreaterThan(String name, Comparable<?> value) {
        sb.append(name);
        params.add(value);
        if (isNegated()) {
            sb.append(" <= ?");
        }
        else {
            sb.append(" > ?");
        }
    }

    @Override
    public void appendGreaterThanOrEquals(String name, Comparable<?> value) {
        sb.append(name);
        params.add(value);
        if (isNegated()) {
            sb.append(" < ?");
        }
        else {
            sb.append(" >= ?");
        }
    }

    @Override
    public void appendLessThan(String name, Comparable<?> value) {
        sb.append(name);
        params.add(value);
        if (isNegated()) {
            sb.append(" >= ?");
        }
        else {
            sb.append(" < ?");
        }
    }

    @Override
    public void appendLessThanOrEquals(String name, Comparable<?> value) {
        sb.append(name);
        params.add(value);
        if (isNegated()) {
            sb.append(" > ?");
        }
        else {
            sb.append(" <= ?");
        }
    }

}
