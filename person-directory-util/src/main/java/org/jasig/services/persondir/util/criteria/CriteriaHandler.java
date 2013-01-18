package org.jasig.services.persondir.util.criteria;

import org.jasig.services.persondir.criteria.BinaryLogicCriteria;
import org.jasig.services.persondir.criteria.CompareCriteria;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.criteria.NotCriteria;

/**
 * Handler for nodes in a {@link Criteria} tree. Within each callback child
 * nodes should be handled by calling {@link CriteriaWalker#walkCriteria(Criteria)}
 */
public interface CriteriaHandler {
    void handleBinaryLogicCriteria(BinaryLogicCriteria c, CriteriaWalker walker);
    void handleCompareCriteria(CompareCriteria<?> c, CriteriaWalker walker);
    void handleNotCriteria(NotCriteria c, CriteriaWalker walker);
}
