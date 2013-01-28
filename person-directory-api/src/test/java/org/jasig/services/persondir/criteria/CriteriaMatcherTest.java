package org.jasig.services.persondir.criteria;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class CriteriaMatcherTest {
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
    public void testEqualsCriteria() {
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
    
    @Test(expected=IllegalArgumentException.class)
    public void testGreaterThanNullCriteria() {
        new GreaterThanCriteria("attr", null);
    }
    
    @Test
    public void testGreaterThanCriteria() {
        Criteria c = new GreaterThanCriteria("attr", 31);
        
        Map<String, List<Object>> attributes = new HashMap<String, List<Object>>();
        
        attributes.put("attr", null);
        assertFalse(c.matches(attributes));
        
        attributes.put("attr", Collections.emptyList());
        assertFalse(c.matches(attributes));
        
        attributes.put("attr", Collections.<Object>singletonList("value"));
        assertFalse(c.matches(attributes));
        
        attributes.put("attr", Arrays.<Object>asList(40, null));
        assertTrue(c.matches(attributes));
        
        attributes.put("attr", Arrays.<Object>asList(20, null));
        assertFalse(c.matches(attributes));

        attributes.put("attr", Arrays.<Object>asList("40", null));
        assertTrue(c.matches(attributes));
        
        attributes.put("attr", Arrays.<Object>asList("20", null));
        assertFalse(c.matches(attributes));
        
        //TODO date test
    }
    
    
    
    
    
    @Ignore
    @Test
    public void testCriteriaMatches() {
        Criteria c =
                CriteriaBuilder.and(
                    CriteriaBuilder.not(CriteriaBuilder.eq("phone", null)),
                    CriteriaBuilder.eq("firstName", "jane"),
                    CriteriaBuilder.gt("age", 13),
                    CriteriaBuilder.gte("weight", 14),
                    CriteriaBuilder.lt("height", 15),
                    CriteriaBuilder.lte("length", 16),
                    CriteriaBuilder.like("lastName", "Dalq*")
            );
        
        Map<String, List<Object>> attributes = ImmutableMap.<String, List<Object>>builder()
            .put("phone", Arrays.<Object>asList("608-698-4512"))
            .put("firstName", Arrays.<Object>asList("jane"))
            .put("age", Arrays.<Object>asList(14))
            .put("weight", Arrays.<Object>asList(32))
            .put("height", Arrays.<Object>asList(2))
            .put("length", Arrays.<Object>asList(16))
            .put("lastName", Arrays.<Object>asList("Dalquist"))
            .build();
        
        assertTrue(c.matches(attributes));
        
        attributes = ImmutableMap.<String, List<Object>>builder()
                .put("phone", Arrays.<Object>asList("608-698-4512"))
                .put("firstName", Arrays.<Object>asList("jane"))
                .put("age", Arrays.<Object>asList(14))
                .put("weight", Arrays.<Object>asList(32))
                .put("length", Arrays.<Object>asList(16))
                .put("lastName", Arrays.<Object>asList("Hight"))
                .build();
            
//        assertFalse(c.matches(attributes));
        
    }
}
