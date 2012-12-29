package org.jasig.services.persondir.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.PersonAttributes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

@RunWith(MockitoJUnitRunner.class)
public class NamedParameterJdbcPersonSourceTest {
    @InjectMocks private NamedParameterJdbcPersonSource personSource;
    @Mock private PerQueryCustomizableJdbcOperations jdbcOperations;
    @Mock private ResultSetExtractor<List<PersonAttributes>> resultSetExtractor;
    @Mock private AttributeQuery<Map<String, Object>> query;
    
    @Test
    public void test() {
        final String sql = "SELECT NAME FROM USERS WHERE USERNAME=:username";
        final Map<String, Object> searchAttributes = ImmutableMap.<String, Object>of("username", "jdoe");
        
        personSource.setQueryTemplate(sql);
        
        when(jdbcOperations.doNamedWithSettings(any(Function.class), anyInt(), anyInt())).thenReturn(Collections.<PersonAttributes>emptyList());
        when(query.getQuery()).thenReturn(searchAttributes);
        
        final List<PersonAttributes> result = personSource.searchForAttributes(query);
        
        assertNotNull(result);
        assertEquals(Collections.emptyList(), result);
    }
}
