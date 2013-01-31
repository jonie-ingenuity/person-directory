package org.jasig.services.persondir.criteria;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        
        attributes.put("attr", Arrays.<Object>asList(new Date(1359479807085l), null));
        assertFalse(c.matches(attributes));
        
        final long date = 1359479807085l;
        
        c = new GreaterThanCriteria("attr", new Date(date));
        
        //util.Date vs util.Date
        attributes.put("attr", Arrays.<Object>asList(new Date(date + TimeUnit.DAYS.toMillis(14)), null));
        assertTrue(c.matches(attributes));

        attributes.put("attr", Arrays.<Object>asList(new Date(date - TimeUnit.DAYS.toMillis(14)), null));
        assertFalse(c.matches(attributes));
        
        //util.Date vs sql.Date
        attributes.put("attr", Arrays.<Object>asList(new java.sql.Date(date + TimeUnit.DAYS.toMillis(14)), null));
        assertTrue(c.matches(attributes));
        
        attributes.put("attr", Arrays.<Object>asList(new java.sql.Date(date - TimeUnit.DAYS.toMillis(14)), null));
        assertFalse(c.matches(attributes));
        
        //util.Date vs sql.Timestamp
        attributes.put("attr", Arrays.<Object>asList(new java.sql.Timestamp(date + TimeUnit.DAYS.toMillis(14)), null));
        assertTrue(c.matches(attributes));

        attributes.put("attr", Arrays.<Object>asList(new java.sql.Timestamp(date - TimeUnit.DAYS.toMillis(14)), null));
        assertFalse(c.matches(attributes));
    }

}
