package org.jasig.services.persondir.criteria;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class EqualsCriteriaMatchesTest {
    @Test
    public void testEqualsNullCriteria() {
        Criteria c = new EqualsCriteria("attr", null);
        
        Map<String, List<Object>> attributes = new HashMap<String, List<Object>>();
        
        attributes.put("attr", null);
        assertTrue(c.matches(attributes));
        
        attributes.put("attr", Collections.emptyList());
        assertTrue(c.matches(attributes));
        
        attributes.put("attr", Collections.<Object>singletonList("value"));
        assertFalse(c.matches(attributes));
        
        attributes.put("attr", Arrays.<Object>asList("value", null));
        assertTrue(c.matches(attributes));
    }
    
    @Test
    public void testStringEqualsCriteria() {
        Criteria c = new EqualsCriteria("attr", "value");
        
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
    
    @Test
    public void testNumberEqualsCriteria() {
        Criteria c = new EqualsCriteria("attr", 1);
        
        Map<String, List<Object>> attributes = new HashMap<String, List<Object>>();
        
        attributes.put("attr", null);
        assertFalse(c.matches(attributes));
        
        attributes.put("attr", Collections.emptyList());
        assertFalse(c.matches(attributes));
        
        attributes.put("attr", Collections.<Object>singletonList("1.0"));
        assertTrue(c.matches(attributes));
        
        attributes.put("attr", Arrays.<Object>asList(1, null));
        assertTrue(c.matches(attributes));
        
        attributes.put("attr", Arrays.<Object>asList("2", null));
        assertFalse(c.matches(attributes));
        
        
        c = new EqualsCriteria("attr", "1.2");
        
        attributes.put("attr", Collections.<Object>singletonList(1.2));
        assertTrue(c.matches(attributes));
        
        attributes.put("attr", Arrays.<Object>asList("1.2", null));
        assertTrue(c.matches(attributes));
        
        attributes.put("attr", Arrays.<Object>asList("1.22", null));
        assertFalse(c.matches(attributes));
    }
}
