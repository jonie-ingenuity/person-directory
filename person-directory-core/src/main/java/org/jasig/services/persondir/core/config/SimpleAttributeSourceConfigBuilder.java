package org.jasig.services.persondir.core.config;

import org.jasig.services.persondir.spi.SimpleAttributeSource;
import org.jasig.services.persondir.spi.gate.SimpleAttributeSourceGate;

final class SimpleAttributeSourceConfigBuilder
        extends AbstractAttributeSourceConfigBuilder<SimpleAttributeSourceBuilder, SimpleAttributeSource, SimpleAttributeSourceGate>
        implements SimpleAttributeSourceBuilder, SimpleAttributeSourceConfig {
    
    SimpleAttributeSourceConfigBuilder(SimpleAttributeSource source) {
        super(SimpleAttributeSourceBuilder.class, source);
    }
}
