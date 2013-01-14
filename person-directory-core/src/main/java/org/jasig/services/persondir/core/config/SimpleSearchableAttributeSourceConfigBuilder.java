package org.jasig.services.persondir.core.config;

import org.jasig.services.persondir.spi.SimpleSearchableAttributeSource;
import org.jasig.services.persondir.spi.gate.SimpleSearchableAttributeSourceGate;

final class SimpleSearchableAttributeSourceConfigBuilder 
        extends AbstractSearchableAttributeSourceConfigBuilder<SimpleSearchableAttributeSourceBuilder, SimpleSearchableAttributeSource, SimpleSearchableAttributeSourceGate>
        implements SimpleSearchableAttributeSourceBuilder, SimpleSearchableAttributeSourceConfig {
    
    SimpleSearchableAttributeSourceConfigBuilder(SimpleSearchableAttributeSource source) {
        super(SimpleSearchableAttributeSourceBuilder.class, source);
    }
}
