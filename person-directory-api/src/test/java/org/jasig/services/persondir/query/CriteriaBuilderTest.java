package org.jasig.services.persondir.query;

import static org.junit.Assert.assertEquals;

import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.criteria.CriteriaBuilder;
import org.junit.Test;


public class CriteriaBuilderTest {
    @Test
    public void testCriteriaBuilder() {
        Criteria c =
            CriteriaBuilder.or(
                CriteriaBuilder.and(
                    CriteriaBuilder.eq("phone", null),
                    CriteriaBuilder.eq("firstName", "jane"),
                    CriteriaBuilder.gt("age", 13),
                    CriteriaBuilder.gte("weight", 14),
                    CriteriaBuilder.lt("height", 15),
                    CriteriaBuilder.lte("length", 16),
                    CriteriaBuilder.like("lastName", "Dalq*")
                ),
                CriteriaBuilder.not(
                    CriteriaBuilder.and(
                        CriteriaBuilder.eq("phone", null),
                        CriteriaBuilder.eq("firstName", "jane"),
                        CriteriaBuilder.gt("age", 13),
                        CriteriaBuilder.gte("weight", 14),
                        CriteriaBuilder.lt("height", 15),
                        CriteriaBuilder.lte("length", 16),
                        CriteriaBuilder.like("lastName", "Dalq*")
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
        
        final String string = c.toString();

        assertEquals(
                "(" +
                    "(" +
                        "phone == 'null' && " +
                        "firstName == 'jane' && " +
                        "age > '13' && " +
                        "weight >= '14' && " +
                        "height < '15' && " +
                        "length <= '16' && " +
                        "lastName ~= 'Dalq*'" +
                    ") || " +
                    "(" +
                        "phone != 'null' || " +
                        "firstName != 'jane' || " +
                        "age <= '13' || " +
                        "weight < '14' || " +
                        "height >= '15' || " +
                        "length > '16' || " +
                        "lastName !~ 'Dalq*'" +
                    ") || " +
                    "(" +
                        "phone != 'null' || " +
                        "firstName == 'jane'" +
                    ")" +
                ")", string);
    }
}
