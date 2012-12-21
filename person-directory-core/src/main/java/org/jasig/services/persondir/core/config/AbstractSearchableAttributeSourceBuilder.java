package org.jasig.services.persondir.core.config;

import org.jasig.services.persondir.spi.BaseAttributeSource;

abstract class AbstractSearchableAttributeSourceBuilder<T extends AbstractSearchableAttributeSourceBuilder<T, S>, S extends BaseAttributeSource> 
        extends AbstractAttributeSourceBuilder<T, S> {
    
    private int maxResults;
    
    AbstractSearchableAttributeSourceBuilder(Class<T> type, S source) {
        super(type, source);
    }
    
    /**
     * TODO is this appropriate?
     */
    public final T setMaxResults(int maxResults) {
        this.maxResults = maxResults;
        return this.getThis();
    }

    protected int getMaxResults() {
        return maxResults;
    }
}
