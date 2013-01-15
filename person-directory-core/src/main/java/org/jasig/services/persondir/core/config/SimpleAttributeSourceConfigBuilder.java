package org.jasig.services.persondir.core.config;

import org.jasig.services.persondir.spi.SimpleAttributeSource;

final class SimpleAttributeSourceConfigBuilder
        extends AbstractAttributeSourceConfigBuilder<SimpleAttributeSourceBuilder, SimpleAttributeSource>
        implements SimpleAttributeSourceBuilder, SimpleAttributeSourceConfig {
    
    SimpleAttributeSourceConfigBuilder(SimpleAttributeSource source, String name) {
        super(SimpleAttributeSourceBuilder.class, source, name);
    }
}
