package org.jasig.services.persondir.core.config;

import org.jasig.services.persondir.spi.BaseAttributeSource;

public interface SearchableAttributeSourceConfig<S extends BaseAttributeSource>
        extends AttributeSourceConfig<S>{

    int getMaxResults();

}