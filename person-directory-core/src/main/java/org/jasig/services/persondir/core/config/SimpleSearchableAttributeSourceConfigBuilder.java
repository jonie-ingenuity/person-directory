package org.jasig.services.persondir.core.config;

import org.jasig.services.persondir.spi.SimpleSearchableAttributeSource;

final class SimpleSearchableAttributeSourceConfigBuilder 
        extends AbstractSearchableAttributeSourceConfigBuilder<SimpleSearchableAttributeSourceBuilder, SimpleSearchableAttributeSource>
        implements SimpleSearchableAttributeSourceBuilder, SimpleSearchableAttributeSourceConfig {
    
    SimpleSearchableAttributeSourceConfigBuilder(SimpleSearchableAttributeSource source) {
        super(SimpleSearchableAttributeSourceBuilder.class, source);
    }
}
