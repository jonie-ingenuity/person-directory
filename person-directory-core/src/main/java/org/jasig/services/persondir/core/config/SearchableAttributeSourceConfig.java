package org.jasig.services.persondir.core.config;

import org.jasig.services.persondir.spi.BaseAttributeSource;
import org.jasig.services.persondir.spi.gate.AttributeSourceGate;

public interface SearchableAttributeSourceConfig<S extends BaseAttributeSource, G extends AttributeSourceGate>
        extends AttributeSourceConfig<S, G>{

    int getMaxResults();

}