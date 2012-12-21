package org.jasig.services.persondir.core.config;

import org.jasig.services.persondir.spi.SimpleSearchableAttributeSource;

public final class SimpleSearchableAttributeSourceBuilder extends 
    AbstractSearchableAttributeSourceBuilder<SimpleSearchableAttributeSourceBuilder, SimpleSearchableAttributeSource> {
    
    SimpleSearchableAttributeSourceBuilder(SimpleSearchableAttributeSource source) {
        super(SimpleSearchableAttributeSourceBuilder.class, source);
    }
}
