package org.jasig.services.persondir.criteria;

import org.jasig.services.persondir.criteria.BinaryLogicCriteria.LogicOperation;
import org.jasig.services.persondir.criteria.CompareCriteria.CompareOperation;

public final class CriteriaBuilder {
    private CriteriaBuilder() {
    }
    
    public static Criteria and(Criteria a, Criteria... b) {
        return new BinaryLogicCriteria(LogicOperation.AND, a, b);
    }
    
    public static Criteria or(Criteria a, Criteria... b) {
        return new BinaryLogicCriteria(LogicOperation.OR, a, b);
    }
    
    public static Criteria not(Criteria c) {
        return new NotCriteria(c);
    }
    
    public static Criteria like(String attribute, Object value) {
        return new CompareCriteria(attribute, CompareOperation.LIKE, value);
    }
    
    public static Criteria eq(String attribute, Object value) {
        return new CompareCriteria(attribute, CompareOperation.EQUALS, value);
    }

    public static Criteria gt(String attribute, Object value) {
        return new CompareCriteria(attribute, CompareOperation.GREATER_THAN, value);
    }
    
    public static Criteria gte(String attribute, Object value) {
        return new CompareCriteria(attribute, CompareOperation.GREATER_THAN_OR_EQUALS, value);
    }
    
    public static Criteria lt(String attribute, Object value) {
        return new CompareCriteria(attribute, CompareOperation.LESS_THAN, value);
    }
    
    public static Criteria lte(String attribute, Object value) {
        return new CompareCriteria(attribute, CompareOperation.LESS_THAN_OR_EQUALS, value);
    }
}
