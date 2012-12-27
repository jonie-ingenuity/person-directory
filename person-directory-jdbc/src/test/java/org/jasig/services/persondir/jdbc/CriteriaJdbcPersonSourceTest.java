package org.jasig.services.persondir.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.criteria.CriteriaBuilder;
import org.jasig.services.persondir.spi.AttributeQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

@RunWith(MockitoJUnitRunner.class)
public class CriteriaJdbcPersonSourceTest {
    @InjectMocks private CriteriaJdbcPersonSource personSource;
    @Mock private PerQueryCustomizableJdbcOperations customizableJdbcOperations;
    @Mock private JdbcOperations jdbcOperations;
    @Mock private ResultSetExtractor<List<PersonAttributes>> resultSetExtractor;
    @Mock private AttributeQuery<Criteria> query;
    
    @Test
    public void testSearchForAttributes() {
        final String sql = "SELECT NAME FROM USERS WHERE {}";
        final Criteria criteria = CriteriaBuilder.eq("username", "jdoe");
        
        personSource.setQueryTemplate(sql);
        
        when(query.getQuery()).thenReturn(criteria);
        when(customizableJdbcOperations.doWithSettings(any(Function.class), anyInt(), anyInt())).thenReturn(Collections.<PersonAttributes>emptyList());
        
        final List<PersonAttributes> result = personSource.searchForAttributes(query);
        
        assertNotNull(result);
        assertEquals(Collections.emptyList(), result);
    }
    
    @Test
    public void testCriteriaToSql() {
        Criteria c = CriteriaBuilder.and(
            CriteriaBuilder.eq("firstName", "jane"),
            CriteriaBuilder.or(
                CriteriaBuilder.eq("lastName", "doe"),
                CriteriaBuilder.eq("lastName", "smith")
            )
        );

        final List<Object> params = new LinkedList<Object>();
        final String sql = personSource.generateSqlCriteria(c, params);
        
        assertEquals("( firstName = ? OR ( lastName = ? AND  lastName = ?))", sql);
        assertEquals(ImmutableList.of("jane", "doe", "smith"), params);
    }
    
    @Test
    public void testNotCriteriaToSql() {
        Criteria c = CriteriaBuilder.and(
            CriteriaBuilder.eq("firstName", "jane"),
            CriteriaBuilder.not(
                CriteriaBuilder.or(
                    CriteriaBuilder.eq("lastName", "doe"),
                    CriteriaBuilder.eq("lastName", "smith")
                )
            )
        );

        final List<Object> params = new LinkedList<Object>();
        final String sql = personSource.generateSqlCriteria(c, params);
        
        assertEquals("( firstName = ? OR ( lastName <> ? OR  lastName <> ?))", sql);
        assertEquals(ImmutableList.of("jane", "doe", "smith"), params);
    }
    
    @Test
    public void testAllCriteriaToSql() {
        Criteria c =
        CriteriaBuilder.or(
            CriteriaBuilder.and(
                CriteriaBuilder.eq("phone", null),
                CriteriaBuilder.eq("firstName", "jane"),
                CriteriaBuilder.gt("age", 13),
                CriteriaBuilder.gte("weight", 14),
                CriteriaBuilder.lt("height", 15),
                CriteriaBuilder.lte("length", 16),
                CriteriaBuilder.like("lastName", "Dalq%")
            ),
            CriteriaBuilder.not(
                CriteriaBuilder.and(
                    CriteriaBuilder.eq("phone", null),
                    CriteriaBuilder.eq("firstName", "jane"),
                    CriteriaBuilder.gt("age", 13),
                    CriteriaBuilder.gte("weight", 14),
                    CriteriaBuilder.lt("height", 15),
                    CriteriaBuilder.lte("length", 16),
                    CriteriaBuilder.like("lastName", "Dalq%")
                )
            ),
            CriteriaBuilder.not(
                CriteriaBuilder.and(
                    CriteriaBuilder.eq("phone", null),
                    CriteriaBuilder.not(
                        CriteriaBuilder.eq("firstName", "jane")
                    )
                )
            )
        );

        final List<Object> params = new LinkedList<Object>();
        final String sql = personSource.generateSqlCriteria(c, params);
        
        assertEquals(
            "(" +
        		"( " +
        		    "phone IS NULL OR  " +
        		    "firstName = ? OR  " +
        		    "age > ? OR  " +
        		    "weight >= ? OR  " +
        		    "height < ? OR  " +
        		    "length <= ? OR  " +
        		    "lastName LIKE ?" +
    		    ") AND " +
    		    "( " +
    		        "phone IS NOT NULL AND  " +
    		        "firstName <> ? AND  " +
    		        "age <= ? AND  " +
    		        "weight < ? AND  " +
    		        "height >= ? AND  " +
    		        "length > ? AND  " +
    		        "lastName NOT LIKE ?" +
		        ") AND " +
		        "( " +
		            "phone IS NOT NULL AND  " +
		            "firstName = ?" +
	            ")" +
            ")", sql);

        assertEquals(ImmutableList.<Object>of("jane", 13, 14, 15, 16, "Dalq%", "jane", 13, 14, 15, 16, "Dalq%", "jane"), params);
    }
}
