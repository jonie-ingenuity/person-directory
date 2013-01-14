package org.jasig.services.persondir.core;

import java.util.Comparator;

import org.jasig.services.persondir.core.config.AttributeSourceConfig;
import org.jasig.services.persondir.core.config.CriteriaSearchableAttributeSourceConfig;
import org.jasig.services.persondir.core.config.SimpleAttributeSourceConfig;
import org.jasig.services.persondir.core.config.SimpleSearchableAttributeSourceConfig;
import org.springframework.core.OrderComparator;

class AttributeSourceConfigComparator implements Comparator<AttributeSourceConfig<?, ?>> {
    public static final AttributeSourceConfigComparator INSTANCE = new AttributeSourceConfigComparator();

    @Override
    public int compare(AttributeSourceConfig<?, ?> o1, AttributeSourceConfig<?, ?> o2) {
        @SuppressWarnings("rawtypes")
        final Class<? extends AttributeSourceConfig> c1 = o1.getClass();
        @SuppressWarnings("rawtypes")
        final Class<? extends AttributeSourceConfig> c2 = o2.getClass();
        
        final boolean c1CriteriaSearch = CriteriaSearchableAttributeSourceConfig.class.isAssignableFrom(c1);
        final boolean c1SimpleSearch = SimpleSearchableAttributeSourceConfig.class.isAssignableFrom(c1);
        final boolean c1Simple = SimpleAttributeSourceConfig.class.isAssignableFrom(c1);
        
        final boolean c2CriteriaSearch = CriteriaSearchableAttributeSourceConfig.class.isAssignableFrom(c2);
        final boolean c2SimpleSearch = SimpleSearchableAttributeSourceConfig.class.isAssignableFrom(c2);
        final boolean c2Simple = SimpleAttributeSourceConfig.class.isAssignableFrom(c2);
        
        //If the classes are the same
        if ((c1CriteriaSearch && c2CriteriaSearch) || (c1SimpleSearch && c2SimpleSearch) || (c1Simple && c2Simple)) {
            return OrderComparator.INSTANCE.compare(o1, o2);
        }
        
        //Order is criteria search -> simple search -> simple
        if (c1CriteriaSearch || (c1SimpleSearch && !c2CriteriaSearch) || (c1Simple && !c2CriteriaSearch && !c2SimpleSearch)) {
            return -1;
        }
        
        return 1;
    }
}