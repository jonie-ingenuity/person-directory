package org.jasig.services.persondir.core.config;

import org.jasig.services.persondir.spi.BaseAttributeSource;

abstract class AbstractSearchableAttributeSourceConfigBuilder<
            T extends SearchableAttributeSourceBuilder<T>, 
            S extends BaseAttributeSource> 
        extends AbstractAttributeSourceConfigBuilder<T, S> 
        implements SearchableAttributeSourceBuilder<T>, SearchableAttributeSourceConfig<S> {
    
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
