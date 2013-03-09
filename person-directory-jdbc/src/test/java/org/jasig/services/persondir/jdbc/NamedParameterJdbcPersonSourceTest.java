package org.jasig.services.persondir.jdbc;

import static org.junit.Assert.assertNull;
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
    
    @Test
    @SuppressWarnings("unchecked")
    public void test() {
        final String sql = "SELECT NAME FROM USERS WHERE USERNAME=:username";
        final Map<String, Object> searchAttributes = ImmutableMap.<String, Object>of("username", "jdoe");
        
        personSource.setQueryTemplate(sql);
        
        when(jdbcOperations.doNamedWithSettings(any(Function.class), anyInt(), anyInt())).thenReturn(Collections.<PersonAttributes>emptyList());

        final AttributeQuery<Map<String, Object>> query = new AttributeQuery<Map<String,Object>>(searchAttributes, 1, 1);
        final PersonAttributes result = personSource.findPersonAttributes(query);
        
        assertNull(result);
    }
}
