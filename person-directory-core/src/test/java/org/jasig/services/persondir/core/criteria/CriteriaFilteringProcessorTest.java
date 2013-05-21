package org.jasig.services.persondir.core.criteria;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.jasig.services.persondir.core.util.NullFunction;
import org.jasig.services.persondir.criteria.AndCriteria;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.criteria.CriteriaBuilder;
import org.jasig.services.persondir.criteria.EqualsCriteria;
import org.jasig.services.persondir.criteria.NotCriteria;
import org.jasig.services.persondir.criteria.OrCriteria;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public class CriteriaFilteringProcessorTest {
    @Test
    public void testSingleCriteriaIncluded() {
        final CriteriaFilteringProcessor criteriaFilteringProcessor = new CriteriaFilteringProcessor(Functions.<String>identity());
        
        final Criteria c = new EqualsCriteria("attr", "value");
        c.process(criteriaFilteringProcessor);
        
        final Criteria filtered = criteriaFilteringProcessor.getRootCriteria();
        assertEquals(c, filtered);
    }

    @Test
    public void testSingleCriteriaExcluded() {
        final CriteriaFilteringProcessor criteriaFilteringProcessor = new CriteriaFilteringProcessor(NullFunction.<String, String>instance());
        
        final Criteria c = new EqualsCriteria("attr", "value");
        c.process(criteriaFilteringProcessor);
        
        final Criteria filtered = criteriaFilteringProcessor.getRootCriteria();
        assertNull(filtered);
    }
    
    @Test
    public void testSingleNotCriteriaIncluded() {
        final CriteriaFilteringProcessor criteriaFilteringProcessor = new CriteriaFilteringProcessor(Functions.<String>identity());
        
        final Criteria c = new NotCriteria(new EqualsCriteria("attr", "value"));
        c.process(criteriaFilteringProcessor);
        
        final Criteria filtered = criteriaFilteringProcessor.getRootCriteria();
        assertEquals(c, filtered);
    }

    @Test
    public void testSingleNotCriteriaExcluded() {
        final CriteriaFilteringProcessor criteriaFilteringProcessor = new CriteriaFilteringProcessor(NullFunction.<String, String>instance());
        
        final Criteria c = new NotCriteria(new EqualsCriteria("attr", "value"));
        c.process(criteriaFilteringProcessor);
        
        final Criteria filtered = criteriaFilteringProcessor.getRootCriteria();
        assertNull(filtered);
    }
    
    @Test
    public void testSingleAndCriteriaIncluded() {
        final CriteriaFilteringProcessor criteriaFilteringProcessor = new CriteriaFilteringProcessor(Functions.<String>identity());
        
        final Criteria ec = new EqualsCriteria("attr", "value");
        final Criteria c = new AndCriteria(ec);
        c.process(criteriaFilteringProcessor);
        
        final Criteria filtered = criteriaFilteringProcessor.getRootCriteria();
        assertEquals(ec, filtered);
    }

    @Test
    public void testSingleAndCriteriaExcluded() {
        final CriteriaFilteringProcessor criteriaFilteringProcessor = new CriteriaFilteringProcessor(NullFunction.<String, String>instance());
        
        final Criteria c = new AndCriteria(new EqualsCriteria("attr", "value"));
        c.process(criteriaFilteringProcessor);
        
        final Criteria filtered = criteriaFilteringProcessor.getRootCriteria();
        assertNull(filtered);
    }
    
    @Test
    public void testSingleOrCriteriaIncluded() {
        final CriteriaFilteringProcessor criteriaFilteringProcessor = new CriteriaFilteringProcessor(Functions.<String>identity());
        
        final Criteria ec = new EqualsCriteria("attr", "value");
        final Criteria c = new OrCriteria(ec);
        c.process(criteriaFilteringProcessor);
        
        final Criteria filtered = criteriaFilteringProcessor.getRootCriteria();
        assertEquals(ec, filtered);
    }

    @Test
    public void testSingleOrCriteriaExcluded() {
        final CriteriaFilteringProcessor criteriaFilteringProcessor = new CriteriaFilteringProcessor(NullFunction.<String, String>instance());
        
        final Criteria c = new OrCriteria(new EqualsCriteria("attr", "value"));
        c.process(criteriaFilteringProcessor);
        
        final Criteria filtered = criteriaFilteringProcessor.getRootCriteria();
        assertNull(filtered);
    }
    
    @Test
    public void testMultipleAndCriteriaIncExc() {
        @SuppressWarnings("unchecked")
        final Function<String, String> filter = mock(Function.class);
        final CriteriaFilteringProcessor criteriaFilteringProcessor = new CriteriaFilteringProcessor(filter);
        
        when(filter.apply(anyString())).thenAnswer(new Answer<String>() {
            private int count = 0;
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                if ((count++) % 2 == 0) {
                    return (String)invocation.getArguments()[0];
                }
                return null;
            }
        });
        
        final Criteria c = new AndCriteria(new EqualsCriteria("attr1", "value1"), new EqualsCriteria("attr2", "value2"), new EqualsCriteria("attr3", "value3"));
        c.process(criteriaFilteringProcessor);
        
        final Criteria filtered = criteriaFilteringProcessor.getRootCriteria();
        final Criteria ec = new AndCriteria(new EqualsCriteria("attr1", "value1"), new EqualsCriteria("attr3", "value3"));
        assertEquals(ec, filtered);
    }
    
    @Test
    public void testMultipleOrCriteriaIncExc() {
        @SuppressWarnings("unchecked")
        final Function<String, String> filter = mock(Function.class);
        final CriteriaFilteringProcessor criteriaFilteringProcessor = new CriteriaFilteringProcessor(filter);
        
        when(filter.apply(anyString())).thenAnswer(new Answer<String>() {
            private int count = 0;
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                if ((count++) % 2 == 0) {
                    return (String)invocation.getArguments()[0];
                }
                return null;
            }
        });
        
        final Criteria c = new OrCriteria(new EqualsCriteria("attr1", "value1"), new EqualsCriteria("attr2", "value2"), new EqualsCriteria("attr3", "value3"));
        c.process(criteriaFilteringProcessor);
        
        final Criteria filtered = criteriaFilteringProcessor.getRootCriteria();
        final Criteria ec = new OrCriteria(new EqualsCriteria("attr1", "value1"), new EqualsCriteria("attr3", "value3"));
        assertEquals(ec, filtered);
    }
    
    @Test
    public void testComplexCriteria() {
        final CriteriaFilteringProcessor criteriaFilteringProcessor = new CriteriaFilteringProcessor(new RandomFilter());
        
        final Criteria c =
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
                        CriteriaBuilder.or(
                            CriteriaBuilder.eq("phone", null),
                            CriteriaBuilder.not(
                                CriteriaBuilder.eq("firstName", "jane")
                            )
                        )
                    )
                );
        c.process(criteriaFilteringProcessor);
        
        final Criteria filtered = criteriaFilteringProcessor.getRootCriteria();
        final Criteria ec =
                CriteriaBuilder.or(
                        CriteriaBuilder.and(
                            CriteriaBuilder.eq("phone", null),
                            CriteriaBuilder.gt("age", 13),
                            CriteriaBuilder.lte("length", 16),
                            CriteriaBuilder.like("lastName", "Dalq*")
                        ),
                        CriteriaBuilder.not(
                            CriteriaBuilder.and(
                                CriteriaBuilder.eq("phone", null),
                                CriteriaBuilder.gt("age", 13),
                                CriteriaBuilder.lte("length", 16),
                                CriteriaBuilder.like("lastName", "Dalq*")
                            )
                        ),
                        CriteriaBuilder.not(CriteriaBuilder.eq("phone", null))
                    );
        assertEquals(ec, filtered);
    }
}
