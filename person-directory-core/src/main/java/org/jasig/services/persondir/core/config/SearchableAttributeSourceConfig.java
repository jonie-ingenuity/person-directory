package org.jasig.services.persondir.core.config;

import org.jasig.services.persondir.spi.BaseAttributeSource;

/**
 * @author Eric Dalquist
 * @param <S> The {@link BaseAttributeSource} type that is being configured
 */
public interface SearchableAttributeSourceConfig<S extends BaseAttributeSource>
        extends AttributeSourceConfig<S>{

    /**
     * @return The maximum number of results to be returned.
     * 
     * @see SearchableAttributeSourceBuilder#setMaxResults(int)
     */
    int getMaxResults();

}