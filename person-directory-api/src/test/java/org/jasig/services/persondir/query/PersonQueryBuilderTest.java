package org.jasig.services.persondir.query;

import org.jasig.services.persondir.criteria.BinaryLogicCriteria;
import org.jasig.services.persondir.criteria.BinaryLogicCriteria.LogicOperation;
import org.jasig.services.persondir.criteria.CompareCriteria;
import org.jasig.services.persondir.criteria.CompareCriteria.CompareOperation;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.criteria.CriteriaBuilder;



public class PersonQueryBuilderTest {
    public void testBuildPersonQuery() {
        /*
         * username=doe
         */
        new CompareCriteria("username", CompareOperation.EQUALS, "doe");

        /*
         * firstName=jane && (lastName=doe or lastName=smith)
         */
        final Criteria explicit = new BinaryLogicCriteria(LogicOperation.AND, 
                new CompareCriteria("firstName", CompareOperation.EQUALS, "jane"),
                new BinaryLogicCriteria(LogicOperation.OR,
                        new CompareCriteria("lastName", CompareOperation.EQUALS, "doe"),
                        new CompareCriteria("lastName", CompareOperation.EQUALS, "smith")
                )
        );
        
        final Criteria builder = CriteriaBuilder.and(
                CriteriaBuilder.eq("firstName", "jane"),
                CriteriaBuilder.or(
                        CriteriaBuilder.eq("lastName", "doe"),
                        CriteriaBuilder.eq("lastName", "smith")
                )
        );

        //TODO assertEquals(explicit, builder);
    }
}
