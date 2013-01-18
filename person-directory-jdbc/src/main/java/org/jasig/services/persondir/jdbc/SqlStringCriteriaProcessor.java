package org.jasig.services.persondir.jdbc;

import java.util.ArrayList;
import java.util.List;

import org.jasig.services.persondir.criteria.CriteriaProcessor;

public class SqlStringCriteriaProcessor implements CriteriaProcessor {
    private final StringBuilder sb = new StringBuilder();
    private final List<Object> params = new ArrayList<Object>();
    private boolean negated = false;
    
    @Override
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
    public void appendAndStart() {
        sb.append("(");
    }

    @Override
    public void appendAndSeperator() {
        if (negated) {
            sb.append(" OR ");
        }
        else {
            sb.append(" AND ");
        }
    }

    @Override
    public void appendAndEnd() {
        sb.append(")");
    }

    @Override
    public void appendOrStart() {
        sb.append("(");
    }

    @Override
    public void appendOrSeperator() {
        if (negated) {
            sb.append(" AND ");
        }
        else {
            sb.append(" OR ");
        }
    }

    @Override
    public void appendOrEnd() {
        sb.append(")");
    }

    @Override
    public void appendNotStart() {
        negated = !negated;
    }

    @Override
    public void appendNotEnd() {
        negated = !negated;
    }

    @Override
    public void appendEquals(String name, Object value) {
        sb.append(name);
        if (value != null) {
            params.add(value);
            if (negated) {
                sb.append(" <> ?");
            }
            else {
                sb.append(" = ?");
            }
        }
        else {
            if (negated) {
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
        if (negated) {
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
        if (negated) {
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
        if (negated) {
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
        if (negated) {
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
        if (negated) {
            sb.append(" > ?");
        }
        else {
            sb.append(" <= ?");
        }
    }

}
