package org.jasig.services.persondir.core.config;

import org.jasig.services.persondir.spi.SimpleAttributeSource;

public final class SimpleAttributeSourceBuilder extends 
        AbstractAttributeSourceBuilder<SimpleAttributeSourceBuilder, SimpleAttributeSource> {
    
    SimpleAttributeSourceBuilder(SimpleAttributeSource source) {
        super(SimpleAttributeSourceBuilder.class, source);
    }
}
