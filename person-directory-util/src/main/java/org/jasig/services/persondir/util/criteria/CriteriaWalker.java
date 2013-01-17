package org.jasig.services.persondir.util.criteria;

import org.jasig.services.persondir.criteria.BinaryLogicCriteria;
import org.jasig.services.persondir.criteria.CompareCriteria;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.criteria.NotCriteria;
import org.springframework.util.Assert;

/**
 * Utility to walk a {@link Criteria} tree
 */
public abstract class CriteriaWalker {
    private CriteriaWalker() {
    }
    
    /**
     * Walk the specified {@link Criteria} tree calling back to the {@link CriteriaHandler}
     * for each node.
     */
    public static void walkCriteria(Criteria c, CriteriaHandler handler) {
        Assert.notNull(c, "Criteria must not be null");
        Assert.notNull(handler, "CriteriaHandler must not be null");
        
        final CriteriaWalkerImpl walker = new CriteriaWalkerImpl(handler);
        walker.walkCriteria(c);
    }
    
    abstract CriteriaHandler getCriteriaHandler();
    
    public void walkCriteria(Criteria c) {
        Assert.notNull(c, "Criteria must not be null");
        
        final CriteriaHandler handler = this.getCriteriaHandler();
        if (c instanceof BinaryLogicCriteria) {
            handler.handleBinaryLogicCriteria((BinaryLogicCriteria)c, this);
        }
        else if (c instanceof CompareCriteria) {
            handler.handleCompareCriteria((CompareCriteria)c, this);
        }
        else if (c instanceof NotCriteria) {
            handler.handleNotCriteria((NotCriteria)c, this);
        }
        else {
            throw new IllegalArgumentException(c.getClass() + " is not a supported Criteria implementation");
        }
    }
    
    private static class CriteriaWalkerImpl extends CriteriaWalker {
        private final CriteriaHandler criteriaHandler;
        
        public CriteriaWalkerImpl(CriteriaHandler criteriaHandler) {
            super();
            this.criteriaHandler = criteriaHandler;
        }

        @Override
        CriteriaHandler getCriteriaHandler() {
            return this.criteriaHandler;
        }
    }
}
