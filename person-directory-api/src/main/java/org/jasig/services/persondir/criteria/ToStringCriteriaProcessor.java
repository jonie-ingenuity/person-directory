package org.jasig.services.persondir.criteria;


class ToStringCriteriaProcessor implements CriteriaProcessor {
    private final StringBuilder sb = new StringBuilder();
    private boolean negated = false;
    
    @Override
    public String toString() {
        return sb.toString();
    }
    
    @Override
    public void appendAndStart() {
        sb.append("(");
    }

    @Override
    public void appendAndSeperator() {
        if (negated) {
            sb.append(" || ");
        }
        else {
            sb.append(" && ");
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
            sb.append(" && ");
        }
        else {
            sb.append(" || ");
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
        if (negated) {
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
        if (negated) {
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
        if (negated) {
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
        if (negated) {
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
        if (negated) {
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
        if (negated) {
            sb.append(" > ");
        }
        else {
            sb.append(" <= ");
        }
        sb.append("'").append(value).append("'");
    }
}
