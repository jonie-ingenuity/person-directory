package org.jasig.services.persondir.core.config;

import org.jasig.services.persondir.spi.CriteriaSearchableAttributeSource;

public final class CriteriaSearchableAttributeSourceBuilder extends
    AbstractSearchableAttributeSourceBuilder<CriteriaSearchableAttributeSourceBuilder, CriteriaSearchableAttributeSource> {
    
    CriteriaSearchableAttributeSourceBuilder(CriteriaSearchableAttributeSource source) {
        super(CriteriaSearchableAttributeSourceBuilder.class, source);
    }
}
