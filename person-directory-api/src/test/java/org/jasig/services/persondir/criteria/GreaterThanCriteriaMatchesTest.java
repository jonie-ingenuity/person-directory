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

import org.junit.Test;

public class GreaterThanCriteriaMatchesTest {
    @Test(expected=IllegalArgumentException.class)
    public void testGreaterThanNullCriteria() {
        new GreaterThanCriteria("attr", null);
    }
    
    @Test
    public void testGreaterThanNumberCriteria() {
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
        
        attributes.put("attr", Arrays.<Object>asList(31, null));
        assertFalse(c.matches(attributes));

        attributes.put("attr", Arrays.<Object>asList("40", null));
        assertTrue(c.matches(attributes));
        
        attributes.put("attr", Arrays.<Object>asList("20", null));
        assertFalse(c.matches(attributes));
        
        attributes.put("attr", Arrays.<Object>asList(new Date(1359479807085l), null));
        assertFalse(c.matches(attributes));

        
        c = new GreaterThanCriteria("attr", "31");

        assertFalse(c.matches(attributes));
        
        attributes.put("attr", Arrays.<Object>asList(40, null));
        assertTrue(c.matches(attributes));
        
        attributes.put("attr", Arrays.<Object>asList(20, null));
        assertFalse(c.matches(attributes));
        
        attributes.put("attr", Arrays.<Object>asList(31, null));
        assertFalse(c.matches(attributes));

        attributes.put("attr", Arrays.<Object>asList("40", null));
        assertTrue(c.matches(attributes));
        
        attributes.put("attr", Arrays.<Object>asList("20", null));
        assertFalse(c.matches(attributes));
    }

    @Test
    public void testGreaterThanDateCriteria() {
        final long date = 1359479807085l;
        
        Criteria c = new GreaterThanCriteria("attr", new Date(date));
        
        Map<String, List<Object>> attributes = new HashMap<String, List<Object>>();
        
        //util.Date vs util.Date
        attributes.put("attr", Arrays.<Object>asList(new Date(date + TimeUnit.DAYS.toMillis(14)), null));
        assertTrue(c.matches(attributes));

        attributes.put("attr", Arrays.<Object>asList(new Date(date), null));
        assertFalse(c.matches(attributes));

        attributes.put("attr", Arrays.<Object>asList(new Date(date - TimeUnit.DAYS.toMillis(14)), null));
        assertFalse(c.matches(attributes));
        
        //util.Date vs sql.Date
        attributes.put("attr", Arrays.<Object>asList(new java.sql.Date(date + TimeUnit.DAYS.toMillis(14)), null));
        assertTrue(c.matches(attributes));
        
        attributes.put("attr", Arrays.<Object>asList(new java.sql.Date(date), null));
        assertFalse(c.matches(attributes));
        
        attributes.put("attr", Arrays.<Object>asList(new java.sql.Date(date - TimeUnit.DAYS.toMillis(14)), null));
        assertFalse(c.matches(attributes));
        
        //util.Date vs sql.Timestamp
        attributes.put("attr", Arrays.<Object>asList(new java.sql.Timestamp(date + TimeUnit.DAYS.toMillis(14)), null));
        assertTrue(c.matches(attributes));

        attributes.put("attr", Arrays.<Object>asList(new java.sql.Timestamp(date), null));
        assertFalse(c.matches(attributes));

        attributes.put("attr", Arrays.<Object>asList(new java.sql.Timestamp(date - TimeUnit.DAYS.toMillis(14)), null));
        assertFalse(c.matches(attributes));
        
        c = new GreaterThanCriteria("attr", date);
        
        attributes.put("attr", Arrays.<Object>asList(new Date(date + TimeUnit.DAYS.toMillis(14)), null));
        assertFalse(c.matches(attributes));
    }
}
