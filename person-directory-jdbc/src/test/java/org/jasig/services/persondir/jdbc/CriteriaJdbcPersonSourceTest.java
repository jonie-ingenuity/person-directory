package org.jasig.services.persondir.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.criteria.CriteriaBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.google.common.base.Function;

@RunWith(MockitoJUnitRunner.class)
public class CriteriaJdbcPersonSourceTest {
    @InjectMocks private CriteriaJdbcPersonSource personSource;
    @Mock private PerQueryCustomizableJdbcOperations customizableJdbcOperations;
    @Mock private JdbcOperations jdbcOperations;
    @Mock private ResultSetExtractor<List<PersonAttributes>> resultSetExtractor;
    
    @Test
    @SuppressWarnings("unchecked")
    public void testSearchForAttributes() {
        final String sql = "SELECT NAME FROM USERS WHERE {}";
        final Criteria criteria = CriteriaBuilder.eq("username", "jdoe");
        
        personSource.setQueryTemplate(sql);
        
        final AttributeQuery<Criteria> query = new AttributeQuery<Criteria>(criteria, 1, 1);
        when(customizableJdbcOperations.doWithSettings(any(Function.class), anyInt(), anyInt())).thenReturn(Collections.<PersonAttributes>emptyList());
        
        final List<PersonAttributes> result = personSource.searchForAttributes(query);
        
        assertNotNull(result);
        assertEquals(Collections.emptyList(), result);
    }
}
