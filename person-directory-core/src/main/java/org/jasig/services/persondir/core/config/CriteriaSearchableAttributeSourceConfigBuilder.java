package org.jasig.services.persondir.core.config;

import org.jasig.services.persondir.spi.CriteriaSearchableAttributeSource;

final class CriteriaSearchableAttributeSourceConfigBuilder 
    extends AbstractSearchableAttributeSourceConfigBuilder<CriteriaSearchableAttributeSourceBuilder, CriteriaSearchableAttributeSource>
    implements CriteriaSearchableAttributeSourceBuilder, CriteriaSearchableAttributeSourceConfig {
    
    CriteriaSearchableAttributeSourceConfigBuilder(CriteriaSearchableAttributeSource source) {
        super(CriteriaSearchableAttributeSourceBuilder.class, source);
    }
}
