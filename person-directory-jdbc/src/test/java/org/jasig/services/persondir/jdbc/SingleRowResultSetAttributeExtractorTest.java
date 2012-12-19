package org.jasig.services.persondir.jdbc;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jasig.services.persondir.PersonAttributes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SingleRowResultSetAttributeExtractorTest {
    @InjectMocks private SingleRowResultSetAttributeExtractor attributeExtractor;
    @Mock private ResultSet rs;
    @Mock private ResultSetMetaData metaData;
    
    @Test
    public void testNoRowsNoColumns() throws Exception {
        when(rs.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(0);
        
        final List<PersonAttributes> results = attributeExtractor.extractData(rs);
        assertNotNull(results);
        assertEquals(0, results.size());
    }
    
    @Test
    public void testOneRowNoColumns() throws Exception {
        when(rs.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(0);
        
        when(rs.next()).thenReturn(true, false);
        
        final List<PersonAttributes> results = attributeExtractor.extractData(rs);
        assertNotNull(results);
        assertEquals(1, results.size());
        
        PersonAttributes personAttributes = results.get(0);
        assertNotNull(personAttributes);
        
        Map<String, List<Object>> attributes = personAttributes.getAttributes();
        assertNotNull(attributes);
        
        assertEquals(0, attributes.size());
    }
    
    @Test
    public void testOneRowWithColumns() throws Exception {
        when(rs.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(2);
        
        //columns
        when(metaData.getColumnName(1)).thenReturn("first");
        when(metaData.getColumnName(2)).thenReturn("last");
        
        when(rs.next()).thenReturn(true, false);
        
        //row 1
        when(rs.getObject(1)).thenReturn("John");
        when(rs.getObject(2)).thenReturn("Doe");
        
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
        assertEquals(Collections.singletonList("Doe"), attributes.get("last"));
        assertEquals(Collections.singletonList("Doe"), attributes.get("LaSt"));
    }
    
    @Test
    public void testTwoRowsWithColumns() throws Exception {
        when(rs.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(2);
        
        //columns
        when(metaData.getColumnName(1)).thenReturn("first");
        when(metaData.getColumnName(2)).thenReturn("last");
        
        when(rs.next()).thenReturn(true, true, false);
        
        //row 1, 2, ...
        when(rs.getObject(1)).thenReturn("John", "Jane");
        when(rs.getObject(2)).thenReturn("Doe");
        
        
        final List<PersonAttributes> results = attributeExtractor.extractData(rs);
        assertNotNull(results);
        assertEquals(2, results.size());
        
        
        //Verify result 1
        PersonAttributes personAttributes = results.get(0);
        assertNotNull(personAttributes);
        
        Map<String, List<Object>> attributes = personAttributes.getAttributes();
        assertNotNull(attributes);
        
        assertEquals(2, attributes.size());
        
        assertEquals(Collections.singletonList("John"), attributes.get("first"));
        assertEquals(Collections.singletonList("John"), attributes.get("FIRST"));
        assertEquals(Collections.singletonList("Doe"), attributes.get("last"));
        assertEquals(Collections.singletonList("Doe"), attributes.get("LaSt"));

        
        //Verify result 2
        personAttributes = results.get(1);
        assertNotNull(personAttributes);
        
        attributes = personAttributes.getAttributes();
        assertNotNull(attributes);
        
        assertEquals(2, attributes.size());
        
        assertEquals(Collections.singletonList("Jane"), attributes.get("first"));
        assertEquals(Collections.singletonList("Doe"), attributes.get("last"));
    }
}
