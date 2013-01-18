package org.jasig.services.persondir.jdbc;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.criteria.CriteriaBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;

@RunWith(MockitoJUnitRunner.class)
public class CriteriaSqlStringBuilderTest {
    
    @Test
    public void testCriteriaToSql() {
        Criteria c = CriteriaBuilder.and(
            CriteriaBuilder.eq("firstName", "jane"),
            CriteriaBuilder.or(
                CriteriaBuilder.eq("lastName", "doe"),
                CriteriaBuilder.eq("lastName", "smith")
            )
        );

        final CriteriaSqlStringBuilder builder = new CriteriaSqlStringBuilder();
        c.process(builder);
        final List<Object> params = builder.getParams();
        final String sql = builder.toString();
        
        
        assertEquals("(firstName = ? AND (lastName = ? OR lastName = ?))", sql);
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

        final CriteriaSqlStringBuilder builder = new CriteriaSqlStringBuilder();
        c.process(builder);
        final List<Object> params = builder.getParams();
        final String sql = builder.toString();
        
        assertEquals("(firstName = ? AND (lastName <> ? AND lastName <> ?))", sql);
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

        final CriteriaSqlStringBuilder builder = new CriteriaSqlStringBuilder();
        c.process(builder);
        final List<Object> params = builder.getParams();
        final String sql = builder.toString();
        
        assertEquals(
            "(" +
                "(" +
                    "phone IS NULL AND " +
                    "firstName = ? AND " +
                    "age > ? AND " +
                    "weight >= ? AND " +
                    "height < ? AND " +
                    "length <= ? AND " +
                    "lastName LIKE ?" +
                ") OR " +
                "(" +
                    "phone IS NOT NULL OR " +
                    "firstName <> ? OR " +
                    "age <= ? OR " +
                    "weight < ? OR " +
                    "height >= ? OR " +
                    "length > ? OR " +
                    "lastName NOT LIKE ?" +
                ") OR " +
                "(" +
                    "phone IS NOT NULL OR " +
                    "firstName = ?" +
                ")" +
            ")", sql);

        assertEquals(ImmutableList.<Object>of("jane", 13, 14, 15, 16, "Dalq%", "jane", 13, 14, 15, 16, "Dalq%", "jane"), params);
    }
}
