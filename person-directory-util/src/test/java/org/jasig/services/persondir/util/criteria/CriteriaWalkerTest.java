package org.jasig.services.persondir.util.criteria;

import java.util.LinkedList;
import java.util.List;

import org.jasig.services.persondir.criteria.BinaryLogicCriteria;
import org.jasig.services.persondir.criteria.CompareCriteria;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.criteria.CriteriaBuilder;
import org.jasig.services.persondir.criteria.NotCriteria;
import org.junit.Test;


public class CriteriaWalkerTest {

    @Test(expected=IllegalArgumentException.class)
    public void testNullCriteria() {
        final ListCriteriaHandler handler = new ListCriteriaHandler();
        
        CriteriaWalker.walkCriteria(null, handler);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testNullHandler() {
        Criteria c = CriteriaBuilder.and(
            CriteriaBuilder.eq("firstName", "jane"),
            CriteriaBuilder.or(
                CriteriaBuilder.eq("lastName", "doe"),
                CriteriaBuilder.eq("lastName", "smith")
            )
        );
        
        CriteriaWalker.walkCriteria(c, null);
    }
    
    private static final class ListCriteriaHandler implements CriteriaHandler {
        private final List<Criteria> criterias = new LinkedList<Criteria>();

        @Override
        public void handleBinaryLogicCriteria(BinaryLogicCriteria c, CriteriaWalker walker) {
            criterias.add(c);
            for (final Criteria criteria : c.getCriteriaList()) {
                walker.walkCriteria(c);
            }
        }

        @Override
        public void handleCompareCriteria(CompareCriteria c, CriteriaWalker walker) {
            criterias.add(c);
        }

        @Override
        public void handleNotCriteria(NotCriteria c, CriteriaWalker walker) {
            criterias.add(c);
            walker.walkCriteria(c.getCriteria());
        }
    }
}
