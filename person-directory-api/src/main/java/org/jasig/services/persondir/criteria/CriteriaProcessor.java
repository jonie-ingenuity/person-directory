package org.jasig.services.persondir.criteria;

public interface CriteriaProcessor {
    void appendAndStart();
    void appendAndSeperator();
    void appendAndEnd();
    
    void appendOrStart();
    void appendOrSeperator();
    void appendOrEnd();
    
    void appendNotStart();
    void appendNotEnd();
    
    void appendEquals(String name, Object value);
    void appendLike(String name, Object value);
    void appendGreaterThan(String name, Comparable<?> value);
    void appendGreaterThanOrEquals(String name, Comparable<?> value);
    void appendLessThan(String name, Comparable<?> value);
    void appendLessThanOrEquals(String name, Comparable<?> value);
}
