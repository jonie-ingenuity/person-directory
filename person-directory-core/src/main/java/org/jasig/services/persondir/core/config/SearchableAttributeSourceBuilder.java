package org.jasig.services.persondir.core.config;

import org.jasig.services.persondir.spi.gate.AttributeSourceGate;


public interface SearchableAttributeSourceBuilder<T extends SearchableAttributeSourceBuilder<T, G>, G extends AttributeSourceGate>
        extends AttributeSourceBuilder<T, G> {

    T setMaxResults(int maxResults);

}