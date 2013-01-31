package org.jasig.services.persondir.criteria;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class LikeCriteriaMatchesTest {
    @Test(expected=IllegalArgumentException.class)
    public void testLikeNullCriteria() {
        new LikeCriteria("attr", null);
    }
    
    @Test
    public void testLikeCriteria() {
        Criteria c = new LikeCriteria("attr", "val*");
        
        Map<String, List<Object>> attributes = new HashMap<String, List<Object>>();
        
        attributes.put("attr", null);
        assertFalse(c.matches(attributes));
        
        attributes.put("attr", Collections.emptyList());
        assertFalse(c.matches(attributes));
        
        attributes.put("attr", Collections.<Object>singletonList("value"));
        assertTrue(c.matches(attributes));
        
        attributes.put("attr", Arrays.<Object>asList("value", null));
        assertTrue(c.matches(attributes));
        
        attributes.put("attr", Arrays.<Object>asList("foo", null));
        assertFalse(c.matches(attributes));
    }
}
