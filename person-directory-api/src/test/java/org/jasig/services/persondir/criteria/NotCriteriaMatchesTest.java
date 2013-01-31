package org.jasig.services.persondir.criteria;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NotCriteriaMatchesTest {
    @Mock private Criteria mockCriteria;
    
    @Test(expected=IllegalArgumentException.class)
    public void testEqualsNullCriteria() {
        new NotCriteria(null);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testNotFalseCriteria() {
        when(mockCriteria.matches(any(Map.class))).thenReturn(false);
        
        Criteria c = new NotCriteria(mockCriteria);
        
        Map<String, List<Object>> attributes = new HashMap<String, List<Object>>();
        assertTrue(c.matches(attributes));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testNotTrueCriteria() {
        when(mockCriteria.matches(any(Map.class))).thenReturn(true);
        
        Criteria c = new NotCriteria(mockCriteria);
        
        Map<String, List<Object>> attributes = new HashMap<String, List<Object>>();
        assertFalse(c.matches(attributes));
    }
}
