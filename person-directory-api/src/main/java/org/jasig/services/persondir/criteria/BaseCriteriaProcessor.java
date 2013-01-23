package org.jasig.services.persondir.criteria;


/**
 * Helper for implemenations of {@link CriteriaProcessor} so the sub-class doesn't
 * have to implement every method
 * 
 * @author Eric Dalquist
 */
public class BaseCriteriaProcessor implements CriteriaProcessor {
    private boolean negated = false;
    
    public boolean isNegated() {
        return negated;
    }
    @Override
    public void appendAndStart(AndCriteria criteria) {
        this.appendBinaryLogicStart(criteria);
    }
    @Override
    public void appendAndSeperator(AndCriteria criteria) {
        this.appendBinaryLogicSeperator(criteria);
    }
    @Override
    public void appendAndEnd(AndCriteria criteria) {
        this.appendBinaryLogicEnd(criteria);
    }
    @Override
    public void appendOrStart(OrCriteria criteria) {
        this.appendBinaryLogicStart(criteria);        
    }
    @Override
    public void appendOrSeperator(OrCriteria criteria) {
        this.appendBinaryLogicSeperator(criteria);        
    }
    @Override
    public void appendOrEnd(OrCriteria criteria) {
        this.appendBinaryLogicEnd(criteria);        
    }
    /**
     * Called by {@link #appendAndStart()} and {@link #appendOrStart()}
     */
    public void appendBinaryLogicStart(BinaryLogicCriteria criteria) {
    }
    /**
     * Called by {@link #appendAndSeperator())} and {@link #appendOrSeperator()}
     */
    public void appendBinaryLogicSeperator(BinaryLogicCriteria criteria) {
    }
    /**
     * Called by {@link #appendAndEnd()} and {@link #appendOrEnd()}
     */
    public void appendBinaryLogicEnd(BinaryLogicCriteria criteria) {
    }
    @Override
    public void appendNotStart(NotCriteria criteria) {
        negated = !negated;
    }
    @Override
    public void appendNotEnd(NotCriteria criteria) {
        negated = !negated;
    }
    @Override
    public void appendEquals(EqualsCriteria criteria) {
        this.appendCompare(criteria);
        this.appendEquals(criteria.getAttribute(), criteria.getValue());
    }
    @Override
    public void appendLike(LikeCriteria criteria) {
        this.appendCompare(criteria);
        this.appendLike(criteria.getAttribute(), criteria.getValue());        
    }
    @Override
    public void appendGreaterThan(GreaterThanCriteria criteria) {
        this.appendCompare(criteria);
        this.appendGreaterThan(criteria.getAttribute(), criteria.getValue());        
    }
    @Override
    public void appendGreaterThanOrEquals(GreaterThanOrEqualsCriteria criteria) {
        this.appendCompare(criteria);
        this.appendGreaterThanOrEquals(criteria.getAttribute(), criteria.getValue());        
    }
    @Override
    public void appendLessThan(LessThanCriteria criteria) {
        this.appendCompare(criteria);
        this.appendLessThan(criteria.getAttribute(), criteria.getValue());
    }
    @Override
    public void appendLessThanOrEquals(LessThanOrEqualsCriteria criteria) {
        this.appendCompare(criteria);
        this.appendLessThanOrEquals(criteria.getAttribute(), criteria.getValue());        
    }
    public void appendEquals(String name, Object value) {
    }
    public void appendLike(String name, Object value) {
    }
    public void appendGreaterThan(String name, Comparable<?> value) {
    }
    public void appendGreaterThanOrEquals(String name, Comparable<?> value) {
    }
    public void appendLessThan(String name, Comparable<?> value) {
    }
    public void appendLessThanOrEquals(String name, Comparable<?> value) {
    }
    /**
     * Called by {@link #appendEquals(String, Object)}, {@link #appendLike(String, Object)}, {@link #appendGreaterThan(String, Comparable)},
     * {@link #appendGreaterThanOrEquals(String, Comparable)}, {@link #appendLessThan(String, Comparable)}, and {@link #appendLessThanOrEquals(String, Comparable)}
     */
    public void appendCompare(CompareCriteria<?> criteria) {
        this.appendCompare(criteria.getAttribute(), criteria.getValue());
    }
    public void appendCompare(String name, Object value) {
    }
}
