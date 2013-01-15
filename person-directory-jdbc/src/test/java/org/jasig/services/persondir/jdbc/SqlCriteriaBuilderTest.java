package org.jasig.services.persondir.jdbc;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.criteria.CriteriaBuilder;
import org.jasig.services.persondir.util.criteria.CriteriaWalker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;

@RunWith(MockitoJUnitRunner.class)
public class SqlCriteriaBuilderTest {
    
    @Test
    public void testCriteriaToSql() {
        Criteria c = CriteriaBuilder.and(
            CriteriaBuilder.eq("firstName", "jane"),
            CriteriaBuilder.or(
                CriteriaBuilder.eq("lastName", "doe"),
                CriteriaBuilder.eq("lastName", "smith")
            )
        );

        final SqlCriteriaBuilder sqlCriteriaBuilder = new SqlCriteriaBuilder();
        CriteriaWalker.walkCriteria(c, sqlCriteriaBuilder);
        
        final List<Object> params = sqlCriteriaBuilder.getParams();
        final String sql = sqlCriteriaBuilder.getSql();
        
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

        final SqlCriteriaBuilder sqlCriteriaBuilder = new SqlCriteriaBuilder();
        CriteriaWalker.walkCriteria(c, sqlCriteriaBuilder);
        
        final List<Object> params = sqlCriteriaBuilder.getParams();
        final String sql = sqlCriteriaBuilder.getSql();
        
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

        final SqlCriteriaBuilder sqlCriteriaBuilder = new SqlCriteriaBuilder();
        CriteriaWalker.walkCriteria(c, sqlCriteriaBuilder);
        
        final List<Object> params = sqlCriteriaBuilder.getParams();
        final String sql = sqlCriteriaBuilder.getSql();
        
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
