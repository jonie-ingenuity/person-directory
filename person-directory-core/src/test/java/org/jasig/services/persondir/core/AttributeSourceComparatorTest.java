package org.jasig.services.persondir.core;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.jasig.services.persondir.core.config.AttributeSourceConfig;
import org.jasig.services.persondir.core.config.CriteriaSearchableAttributeSourceConfig;
import org.jasig.services.persondir.core.config.SimpleAttributeSourceConfig;
import org.jasig.services.persondir.core.config.SimpleSearchableAttributeSourceConfig;
import org.jasig.services.persondir.spi.BaseAttributeSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AttributeSourceComparatorTest {
    @Mock private CriteriaSearchableAttributeSourceConfig cs1;
    @Mock private CriteriaSearchableAttributeSourceConfig cs2;
    @Mock private SimpleSearchableAttributeSourceConfig ss1;
    @Mock private SimpleSearchableAttributeSourceConfig ss2;
    @Mock private SimpleAttributeSourceConfig s1;
    @Mock private SimpleAttributeSourceConfig s2;

    @Test
    public void testSortingWithOrderA() {
        when(cs1.getOrder()).thenReturn(-1);
        when(ss1.getOrder()).thenReturn(-1);
        when(s1.getOrder()).thenReturn(-1);
        
        final List<AttributeSourceConfig<? extends BaseAttributeSource>> configs = Arrays.asList(cs1, cs2, ss1, ss2, s1, s2);
        Collections.shuffle(configs, new Random(0));
        
        Collections.sort(configs, AttributeSourceConfigComparator.INSTANCE);

        assertEquals(cs1, configs.get(0));
        assertEquals(cs2, configs.get(1));
        assertEquals(ss1, configs.get(2));
        assertEquals(ss2, configs.get(3));
        assertEquals(s1, configs.get(4));
        assertEquals(s2, configs.get(5));
    }
    
    @Test
    public void testSortingWithOrderB() {
        when(cs1.getOrder()).thenReturn(1);
        when(ss1.getOrder()).thenReturn(1);
        when(s1.getOrder()).thenReturn(1);
        
        final List<AttributeSourceConfig<? extends BaseAttributeSource>> configs = Arrays.asList(cs1, cs2, ss1, ss2, s1, s2);
        Collections.shuffle(configs, new Random(0));
        
        Collections.sort(configs, AttributeSourceConfigComparator.INSTANCE);

        assertEquals(cs2, configs.get(0));
        assertEquals(cs1, configs.get(1));
        assertEquals(ss2, configs.get(2));
        assertEquals(ss1, configs.get(3));
        assertEquals(s2, configs.get(4));
        assertEquals(s1, configs.get(5));
    }
}
