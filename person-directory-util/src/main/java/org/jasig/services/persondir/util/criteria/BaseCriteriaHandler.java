package org.jasig.services.persondir.util.criteria;

import org.jasig.services.persondir.criteria.BinaryLogicCriteria;
import org.jasig.services.persondir.criteria.CompareCriteria;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.criteria.NotCriteria;

public class BaseCriteriaHandler implements CriteriaHandler {
    private boolean negated = false;

    @Override
    public void handleBinaryLogicCriteria(BinaryLogicCriteria c, CriteriaWalker walker) {
        for (final Criteria criteria : c.getCriteriaList()) {
            walker.walkCriteria(criteria);
        }
    }

    @Override
    public void handleCompareCriteria(CompareCriteria<?> c, CriteriaWalker walker) {
    }

    @Override
    public void handleNotCriteria(NotCriteria c, CriteriaWalker walker) {
        negated = !negated;
        walker.walkCriteria(c.getCriteria());
        negated = !negated;
    }

    protected final boolean isNegated() {
        return negated;
    }
}
