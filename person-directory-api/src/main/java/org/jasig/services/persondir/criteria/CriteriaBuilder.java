package org.jasig.services.persondir.criteria;

import java.util.Collection;

public final class CriteriaBuilder {
    private CriteriaBuilder() {
    }
    
    public static Criteria and(Criteria... c) {
        if (c.length == 1) {
            return c[0];
        }
        return new AndCriteria(c);
    }
    
    public static Criteria and(Collection<Criteria> c) {
        if (c.size() == 1) {
            return c.iterator().next();
        }
        return new AndCriteria(c);
    }
    
    public static Criteria or(Criteria... c) {
        if (c.length == 1) {
            return c[0];
        }
        return new OrCriteria(c);
    }
    
    public static Criteria or(Collection<Criteria> c) {
        if (c.size() == 1) {
            return c.iterator().next();
        }
        return new OrCriteria(c);
    }
    
    public static Criteria not(Criteria c) {
        return new NotCriteria(c);
    }
    
    public static Criteria like(String attribute, String value) {
        return new LikeCriteria(attribute, value);
    }
    
    public static Criteria eq(String attribute, Object value) {
        return new EqualsCriteria(attribute, value);
    }

    public static Criteria gt(String attribute, Comparable<?> value) {
        return new GreaterThanCriteria(attribute, value);
    }
    
    public static Criteria gte(String attribute, Comparable<?> value) {
        return new GreaterThanOrEqualsCriteria(attribute, value);
    }
    
    public static Criteria lt(String attribute, Comparable<?> value) {
        return new LessThanCriteria(attribute, value);
    }
    
    public static Criteria lte(String attribute, Comparable<?> value) {
        return new LessThanOrEqualsCriteria(attribute, value);
    }
}
