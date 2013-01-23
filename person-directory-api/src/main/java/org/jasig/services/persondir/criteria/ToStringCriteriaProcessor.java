package org.jasig.services.persondir.criteria;


class ToStringCriteriaProcessor extends BaseCriteriaProcessor {
    private final StringBuilder sb = new StringBuilder();
    
    @Override
    public String toString() {
        return sb.toString();
    }
    
    @Override
    public void appendBinaryLogicStart(BinaryLogicCriteria criteria) {
        sb.append("(");
    }

    @Override
    public void appendAndSeperator(AndCriteria criteria) {
        if (isNegated()) {
            sb.append(" || ");
        }
        else {
            sb.append(" && ");
        }
    }

    @Override
    public void appendOrSeperator(OrCriteria criteria) {
        if (isNegated()) {
            sb.append(" && ");
        }
        else {
            sb.append(" || ");
        }
    }

    @Override
    public void appendBinaryLogicEnd(BinaryLogicCriteria criteria) {
        sb.append(")");
    }

    @Override
    public void appendEquals(String name, Object value) {
        sb.append(name);
        if (isNegated()) {
            sb.append(" != ");
        }
        else {
            sb.append(" == ");
        }
        sb.append("'").append(value).append("'");
    }

    @Override
    public void appendLike(String name, Object value) {
        sb.append(name);
        if (isNegated()) {
            sb.append(" !~ ");
        }
        else {
            sb.append(" ~= ");
        }
        sb.append("'").append(value).append("'");
    }

    @Override
    public void appendGreaterThan(String name, Comparable<?> value) {
        sb.append(name);
        if (isNegated()) {
            sb.append(" <= ");
        }
        else {
            sb.append(" > ");
        }
        sb.append("'").append(value).append("'");
    }

    @Override
    public void appendGreaterThanOrEquals(String name, Comparable<?> value) {
        sb.append(name);
        if (isNegated()) {
            sb.append(" < ");
        }
        else {
            sb.append(" >= ");
        }
        sb.append("'").append(value).append("'");
    }

    @Override
    public void appendLessThan(String name, Comparable<?> value) {
        sb.append(name);
        if (isNegated()) {
            sb.append(" >= ");
        }
        else {
            sb.append(" < ");
        }
        sb.append("'").append(value).append("'");
    }

    @Override
    public void appendLessThanOrEquals(String name, Comparable<?> value) {
        sb.append(name);
        if (isNegated()) {
            sb.append(" > ");
        }
        else {
            sb.append(" <= ");
        }
        sb.append("'").append(value).append("'");
    }
}
