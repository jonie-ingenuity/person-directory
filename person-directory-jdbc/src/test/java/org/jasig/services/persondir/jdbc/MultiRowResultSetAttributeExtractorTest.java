package org.jasig.services.persondir.jdbc;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jasig.services.persondir.PersonAttributes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@RunWith(MockitoJUnitRunner.class)
public class MultiRowResultSetAttributeExtractorTest {
    private MultiRowResultSetAttributeExtractor attributeExtractor;
    @Mock private ResultSet rs;
    @Mock private ResultSetMetaData metaData;
    
    @Test(expected=IllegalArgumentException.class)
    public void testNoRowsNoColumns() throws Exception {
        this.attributeExtractor = new MultiRowResultSetAttributeExtractor(
                "username", 
                ImmutableMap.<String, Set<String>>of("ATTR_NM", ImmutableSet.<String>of("ATTR_VL")));
        
        when(rs.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(0);
        
        attributeExtractor.extractData(rs);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testOneRowNoColumns() throws Exception {
        this.attributeExtractor = new MultiRowResultSetAttributeExtractor(
                "username", 
                ImmutableMap.<String, Set<String>>of("ATTR_NM", ImmutableSet.<String>of("ATTR_VL")));
        
        when(rs.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(0);
        
        when(rs.next()).thenReturn(true, false);
        
        attributeExtractor.extractData(rs);
    }
    
    @Test
    public void testOneRowWithColumns() throws Exception {
        this.attributeExtractor = new MultiRowResultSetAttributeExtractor(
                "username", 
                ImmutableMap.<String, Set<String>>of("ATTR_NM", ImmutableSet.<String>of("ATTR_VL")));
        
        when(rs.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(3);
        
        //columns
        when(metaData.getColumnName(1)).thenReturn("username");
        when(metaData.getColumnName(2)).thenReturn("ATTR_NM");
        when(metaData.getColumnName(3)).thenReturn("ATTR_VL");
        
        when(rs.next()).thenReturn(true, false);
        
        //row 1
        when(rs.getString(1)).thenReturn("jdoe");
        when(rs.getString(2)).thenReturn("first");
        when(rs.getObject(3)).thenReturn("John");
        
        final List<PersonAttributes> results = attributeExtractor.extractData(rs);
        assertNotNull(results);
        assertEquals(1, results.size());
        
        PersonAttributes personAttributes = results.get(0);
        assertNotNull(personAttributes);
        
        Map<String, List<Object>> attributes = personAttributes.getAttributes();
        assertNotNull(attributes);
        
        assertEquals(2, attributes.size());
        
        assertEquals(Collections.singletonList("John"), attributes.get("first"));
        assertEquals(Collections.singletonList("John"), attributes.get("FIRST"));
        assertEquals(Collections.singletonList("jdoe"), attributes.get("username"));
    }
    
    @Test
    public void testTwoRowsWithColumns() throws Exception {
        this.attributeExtractor = new MultiRowResultSetAttributeExtractor(
                "username", 
                ImmutableMap.<String, Set<String>>of(
                        "ATTR_NM", ImmutableSet.<String>of("ATTR_VL", "ATTR_VL2"),
                        "ATTR_NMA", ImmutableSet.<String>of("ATTR_VLA")));
        
        when(rs.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(3);
        
        //columns
        when(metaData.getColumnName(1)).thenReturn("username");
        when(metaData.getColumnName(2)).thenReturn("ATTR_NM");
        when(metaData.getColumnName(3)).thenReturn("ATTR_VL");
        
        when(rs.next()).thenReturn(true, true, true, false);
        
        //row 1, 2, 3
        when(rs.getString(1)).thenReturn("jdoe", "jdoe", "bsmith");
        when(rs.getString(2)).thenReturn("first", "last", "last");
        when(rs.getObject(3)).thenReturn("John", "Doe", "Smith");
        
        final List<PersonAttributes> results = attributeExtractor.extractData(rs);
        assertNotNull(results);
        assertEquals(2, results.size());
        
        //Result 1
        PersonAttributes personAttributes = results.get(0);
        assertNotNull(personAttributes);
        
        Map<String, List<Object>> attributes = personAttributes.getAttributes();
        assertNotNull(attributes);
        
        assertEquals(3, attributes.size());
        
        assertEquals(Collections.singletonList("John"), attributes.get("first"));
        assertEquals(Collections.singletonList("John"), attributes.get("FIRST"));
        assertEquals(Collections.singletonList("Doe"), attributes.get("last"));
        assertEquals(Collections.singletonList("jdoe"), attributes.get("username"));
        
        //Result 2
        personAttributes = results.get(1);
        assertNotNull(personAttributes);
        
        attributes = personAttributes.getAttributes();
        assertNotNull(attributes);
        
        assertEquals(2, attributes.size());
        
        assertEquals(Collections.singletonList("Smith"), attributes.get("last"));
        assertEquals(Collections.singletonList("bsmith"), attributes.get("username"));
    }
}
