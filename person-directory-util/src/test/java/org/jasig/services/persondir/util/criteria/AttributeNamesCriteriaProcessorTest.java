package org.jasig.services.persondir.util.criteria;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.criteria.EqualsCriteria;
import org.jasig.services.persondir.criteria.OrCriteria;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class AttributeNamesCriteriaProcessorTest {
    @Test
    public void testNameCollection() {
        final AttributeNamesCriteriaProcessor cp = new AttributeNamesCriteriaProcessor();
        
        final Criteria c = new OrCriteria(
                new EqualsCriteria("attr1", 1),
                new EqualsCriteria("attr2", 2),
                new EqualsCriteria("attr3", 3)
            );
        
        c.process(cp);
        
        final Set<String> names = cp.getAttributeNames();
        
        assertEquals(ImmutableSet.of("attr1", "attr2", "attr3"), names);
    }
}
