package org.jasig.services.persondir.criteria;

public interface CriteriaProcessor {
    void appendAndStart(AndCriteria criteria);
    void appendAndSeperator(AndCriteria criteria);
    void appendAndEnd(AndCriteria criteria);
    
    void appendOrStart(OrCriteria criteria);
    void appendOrSeperator(OrCriteria criteria);
    void appendOrEnd(OrCriteria criteria);
    
    void appendNotStart(NotCriteria criteria);
    void appendNotEnd(NotCriteria criteria);
    
    void appendEquals(EqualsCriteria criteria);
    void appendLike(LikeCriteria criteria);
    void appendGreaterThan(GreaterThanCriteria criteria);
    void appendGreaterThanOrEquals(GreaterThanOrEqualsCriteria criteria);
    void appendLessThan(LessThanCriteria criteria);
    void appendLessThanOrEquals(LessThanOrEqualsCriteria criteria);
}
