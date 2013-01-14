package org.jasig.services.persondir.core;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.core.config.PersonDirectoryConfig;
import org.jasig.services.persondir.core.config.SimpleSearchableAttributeSourceConfig;
import org.jasig.services.persondir.spi.SimpleSearchableAttributeSource;
import org.jasig.services.persondir.spi.cache.CacheKeyGenerator;
import org.jasig.services.persondir.spi.gate.SimpleSearchableAttributeSourceGate;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

class SimpleSearchableAttributeQueryWorker 
        extends AbstractAttributeQueryWorker<
            Map<String, Object>, 
            SimpleSearchableAttributeSource, 
            SimpleSearchableAttributeSourceConfig,
            SimpleSearchableAttributeSourceGate> {
    
    public SimpleSearchableAttributeQueryWorker(
            PersonDirectoryConfig personDirectoryConfig,
            SimpleSearchableAttributeSourceConfig sourceConfig) {

        super(personDirectoryConfig, sourceConfig);
    }
    
    @Override
    protected AttributeQueryCallable createQueryCallable(AttributeQuery<Map<String, Object>> filteredQuery) {
        return new SimpleSearchableAttributeQueryCallable(filteredQuery);
    }

    @Override
    protected Serializable generateCacheKey(Map<String, Object> q, CacheKeyGenerator keyGenerator) {
        return keyGenerator.generateCacheKey(q);
    }

    @Override
    protected Map<String, Object> filterQuery(Map<String, Object> q) {
        //Filter the query by using a live-view wrapper that 
        
        final Set<String> requiredQueryAttributes = sourceConfig.getRequiredQueryAttributes();
        final Set<String> optionalQueryAttributes = sourceConfig.getOptionalQueryAttributes();
        return Maps.filterKeys(q, new Predicate<String>() {
            public boolean apply(String input) {
                return requiredQueryAttributes.contains(input) || optionalQueryAttributes.contains(input);
            }
        });
    }
    
    @Override
    protected Set<String> getQueryAttributeNames(Map<String, Object> query) {
        return query.keySet();
    }

    @Override
    protected boolean checkGate(SimpleSearchableAttributeSourceGate gate, AttributeQuery<Map<String, Object>> query) {
        return gate.checkSimpleSearch(query);
    }

    private final class SimpleSearchableAttributeQueryCallable extends AttributeQueryCallable {
        private final AttributeQuery<Map<String, Object>> attributeQuery;
        
        public SimpleSearchableAttributeQueryCallable(AttributeQuery<Map<String, Object>> attributeQuery) {
            this.attributeQuery = attributeQuery;
        }

        @Override
        protected List<PersonAttributes> doQuery() {
            final SimpleSearchableAttributeSource attributeSource = sourceConfig.getAttributeSource();
            return attributeSource.searchForAttributes(this.attributeQuery);
        }
    }
}
