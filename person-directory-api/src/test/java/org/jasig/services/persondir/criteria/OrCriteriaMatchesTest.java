package org.jasig.services.persondir.criteria;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrCriteriaMatchesTest {
    @Mock private Criteria mockCriteriaA;
    @Mock private Criteria mockCriteriaB;
    @Mock private Criteria mockCriteriaC;
    @Mock private Criteria mockCriteriaD;
    
    @Test(expected=NullPointerException.class)
    public void testOrNullCriteria() {
        new OrCriteria((Criteria)null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testOrNullCollectionCriteria() {
        new OrCriteria((Collection<Criteria>)null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testOrEmptyCollectionCriteria() {
        new OrCriteria(Collections.<Criteria>emptyList());
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testAllFalseOrCriteria() {
        when(mockCriteriaA.matches(any(Map.class))).thenReturn(false);
        when(mockCriteriaB.matches(any(Map.class))).thenReturn(false);
        when(mockCriteriaC.matches(any(Map.class))).thenReturn(false);
        when(mockCriteriaD.matches(any(Map.class))).thenReturn(false);
        
        Criteria c = new OrCriteria(mockCriteriaA, mockCriteriaB, mockCriteriaC, mockCriteriaD);
        
        Map<String, List<Object>> attributes = new HashMap<String, List<Object>>();
        assertFalse(c.matches(attributes));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testFirstFalseOrCriteria() {
        when(mockCriteriaA.matches(any(Map.class))).thenReturn(false);
        when(mockCriteriaB.matches(any(Map.class))).thenReturn(true);
        when(mockCriteriaC.matches(any(Map.class))).thenReturn(true);
        when(mockCriteriaD.matches(any(Map.class))).thenReturn(true);
        
        Criteria c = new OrCriteria(mockCriteriaA, mockCriteriaB, mockCriteriaC, mockCriteriaD);
        
        Map<String, List<Object>> attributes = new HashMap<String, List<Object>>();
        assertTrue(c.matches(attributes));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testMidFalseOrCriteria() {
        when(mockCriteriaA.matches(any(Map.class))).thenReturn(true);
        when(mockCriteriaB.matches(any(Map.class))).thenReturn(false);
        when(mockCriteriaC.matches(any(Map.class))).thenReturn(false);
        when(mockCriteriaD.matches(any(Map.class))).thenReturn(true);
        
        Criteria c = new OrCriteria(mockCriteriaA, mockCriteriaB, mockCriteriaC, mockCriteriaD);
        
        Map<String, List<Object>> attributes = new HashMap<String, List<Object>>();
        assertTrue(c.matches(attributes));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testLastFalseOrCriteria() {
        when(mockCriteriaA.matches(any(Map.class))).thenReturn(true);
        when(mockCriteriaB.matches(any(Map.class))).thenReturn(true);
        when(mockCriteriaC.matches(any(Map.class))).thenReturn(true);
        when(mockCriteriaD.matches(any(Map.class))).thenReturn(false);
        
        Criteria c = new OrCriteria(mockCriteriaA, mockCriteriaB, mockCriteriaC, mockCriteriaD);
        
        Map<String, List<Object>> attributes = new HashMap<String, List<Object>>();
        assertTrue(c.matches(attributes));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAllTrueOrCriteria() {
        when(mockCriteriaA.matches(any(Map.class))).thenReturn(true);
        when(mockCriteriaB.matches(any(Map.class))).thenReturn(true);
        when(mockCriteriaC.matches(any(Map.class))).thenReturn(true);
        when(mockCriteriaD.matches(any(Map.class))).thenReturn(true);
        
        Criteria c = new OrCriteria(mockCriteriaA, mockCriteriaB, mockCriteriaC, mockCriteriaD);
        
        Map<String, List<Object>> attributes = new HashMap<String, List<Object>>();
        assertTrue(c.matches(attributes));
    }
}
