package org.jasig.services.persondir.core;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.core.config.CriteriaSearchableAttributeSourceConfig;
import org.jasig.services.persondir.core.config.PersonDirectoryConfig;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.spi.CriteriaSearchableAttributeSource;
import org.jasig.services.persondir.spi.cache.CacheKeyGenerator;
import org.jasig.services.persondir.spi.gate.CriteriaSearchableAttributeSourceGate;

class CriteriaSearchableAttributeQueryWorker 
        extends AbstractAttributeQueryWorker<
            Criteria, 
            CriteriaSearchableAttributeSource, 
            CriteriaSearchableAttributeSourceConfig,
            CriteriaSearchableAttributeSourceGate> {
    
    public CriteriaSearchableAttributeQueryWorker(
            PersonDirectoryConfig personDirectoryConfig,
            CriteriaSearchableAttributeSourceConfig sourceConfig) {
        
        super(personDirectoryConfig, sourceConfig);
    }

    @Override
    protected AttributeQueryCallable createQueryCallable(AttributeQuery<Criteria> filteredQuery) {
        return new CriteriaSearchableAttributeQueryCallable(filteredQuery);
    }

    @Override
    protected Serializable generateCacheKey(Criteria q, CacheKeyGenerator keyGenerator) {
        return keyGenerator.generateCacheKey(q);
    }

    @Override
    protected Criteria filterQuery(Criteria q) {
        // TODO how the heck do we filter criteria queries
        return null;
    }

    @Override
    protected Set<String> getQueryAttributeNames(Criteria query) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean checkGate(CriteriaSearchableAttributeSourceGate gate, AttributeQuery<Criteria> query) {
        return gate.checkCriteriaSearch(query);
    }

    private final class CriteriaSearchableAttributeQueryCallable extends AttributeQueryCallable {
        private final AttributeQuery<Criteria> attributeQuery;
        
        public CriteriaSearchableAttributeQueryCallable(AttributeQuery<Criteria> attributeQuery) {
            this.attributeQuery = attributeQuery;
        }

        @Override
        protected List<PersonAttributes> doQuery() {
            final CriteriaSearchableAttributeSource attributeSource = sourceConfig.getAttributeSource();
            return attributeSource.searchForAttributes(this.attributeQuery);
        }
    }
}
