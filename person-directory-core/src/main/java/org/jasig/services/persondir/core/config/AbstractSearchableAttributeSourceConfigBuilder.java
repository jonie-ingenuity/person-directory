package org.jasig.services.persondir.core.config;

import org.jasig.services.persondir.spi.BaseAttributeSource;
import org.jasig.services.persondir.spi.gate.AttributeSourceGate;

abstract class AbstractSearchableAttributeSourceConfigBuilder<
            T extends SearchableAttributeSourceBuilder<T, G>, 
            S extends BaseAttributeSource,
            G extends AttributeSourceGate> 
        extends AbstractAttributeSourceConfigBuilder<T, S, G> 
        implements SearchableAttributeSourceBuilder<T, G>, SearchableAttributeSourceConfig<S, G> {
    
    private int maxResults;
    
    AbstractSearchableAttributeSourceConfigBuilder(Class<T> type, S source) {
        super(type, source);
    }
    
    @Override
    public final T setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this.getThis();
    }

    @Override
    public final int getMaxResults() {
        return maxResults;
    }
}
