package org.jasig.services.persondir.core.config;

import org.jasig.services.persondir.spi.CriteriaSearchableAttributeSource;
import org.jasig.services.persondir.spi.gate.CriteriaSearchableAttributeSourceGate;

final class CriteriaSearchableAttributeSourceConfigBuilder 
    extends AbstractSearchableAttributeSourceConfigBuilder<CriteriaSearchableAttributeSourceBuilder, CriteriaSearchableAttributeSource, CriteriaSearchableAttributeSourceGate>
    implements CriteriaSearchableAttributeSourceBuilder, CriteriaSearchableAttributeSourceConfig {
    
    CriteriaSearchableAttributeSourceConfigBuilder(CriteriaSearchableAttributeSource source) {
        super(CriteriaSearchableAttributeSourceBuilder.class, source);
    }
}
