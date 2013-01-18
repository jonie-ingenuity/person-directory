package org.jasig.services.persondir.util.criteria;

import org.jasig.services.persondir.criteria.CriteriaProcessor;

/**
 * Helper for implemenations of {@link CriteriaProcessor} so the sub-class doesn't
 * have to implement every method
 * 
 * @author Eric Dalquist
 */
public class BaseCriteriaProcessor implements CriteriaProcessor {
    private boolean negated = false;
    
    @Override
    public void appendAndStart() {
        appendBinaryLogicStart();
    }
    @Override
    public void appendAndSeperator() {
        appendBinaryLogicSeperator();
    }
    @Override
    public void appendAndEnd() {
        appendBinaryLogicEnd();
    }
    @Override
    public void appendOrStart() {
        appendBinaryLogicStart();
    }
    @Override
    public void appendOrSeperator() {
        appendBinaryLogicSeperator();
    }
    @Override
    public void appendOrEnd() {
        appendBinaryLogicEnd();
    }
    /**
     * Called by {@link #appendAndStart()} and {@link #appendOrStart()}
     */
    public void appendBinaryLogicStart() {
    }
    /**
     * Called by {@link #appendAndSeperator())} and {@link #appendOrSeperator()}
     */
    public void appendBinaryLogicSeperator() {
    }
    /**
     * Called by {@link #appendAndEnd()} and {@link #appendOrEnd()}
     */
    public void appendBinaryLogicEnd() {
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
    }
    @Override
    public void appendLike(String name, Object value) {
    }
    @Override
    public void appendGreaterThan(String name, Comparable<?> value) {
    }
    @Override
    public void appendGreaterThanOrEquals(String name, Comparable<?> value) {
    }
    @Override
    public void appendLessThan(String name, Comparable<?> value) {
    }
    @Override
    public void appendLessThanOrEquals(String name, Comparable<?> value) {
    }
    /**
     * Called by {@link #appendEquals(String, Object)}, {@link #appendLike(String, Object)}, {@link #appendGreaterThan(String, Comparable)},
     * {@link #appendGreaterThanOrEquals(String, Comparable)}, {@link #appendLessThan(String, Comparable)}, and {@link #appendLessThanOrEquals(String, Comparable)}
     */
    public void appendCompare(String name, Object value) {
    }
}
