package org.jasig.services.persondir.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.core.config.PersonDirectoryConfig;
import org.jasig.services.persondir.core.config.SimpleAttributeSourceConfig;
import org.jasig.services.persondir.spi.SimpleAttributeSource;
import org.jasig.services.persondir.spi.cache.CacheKeyGenerator;
import org.jasig.services.persondir.spi.gate.SimpleAttributeSourceGate;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

class SimpleAttributeQueryWorker 
        extends AbstractAttributeQueryWorker<
            Map<String, Object>, 
            SimpleAttributeSource, 
            SimpleAttributeSourceConfig,
            SimpleAttributeSourceGate> {
    
    public SimpleAttributeQueryWorker(
            PersonDirectoryConfig personDirectoryConfig,
            SimpleAttributeSourceConfig sourceConfig) {

        super(personDirectoryConfig, sourceConfig);
    }
    
    @Override
    protected AttributeQueryCallable createQueryCallable(AttributeQuery<Map<String, Object>> filteredQuery) {
        return new SimpleAttributeQueryCallable(filteredQuery);
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
    protected boolean checkGate(SimpleAttributeSourceGate gate, AttributeQuery<Map<String, Object>> query) {
        return gate.checkFind(query);
    }


    private final class SimpleAttributeQueryCallable extends AttributeQueryCallable {
        private final AttributeQuery<Map<String, Object>> attributeQuery;
        
        public SimpleAttributeQueryCallable(AttributeQuery<Map<String, Object>> attributeQuery) {
            this.attributeQuery = attributeQuery;
        }

        @Override
        protected List<PersonAttributes> doQuery() {
            final SimpleAttributeSource attributeSource = sourceConfig.getAttributeSource();
            final PersonAttributes result = attributeSource.findPersonAttributes(attributeQuery);
            return Collections.singletonList(result);
        }
    }
}
