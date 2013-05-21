package org.jasig.services.persondir.core.worker;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.core.PersonBuilder;
import org.jasig.services.persondir.core.config.AttributeSourceConfig;
import org.jasig.services.persondir.core.config.CriteriaSearchableAttributeSourceConfig;
import org.jasig.services.persondir.core.config.PersonDirectoryConfig;
import org.jasig.services.persondir.core.criteria.CriteriaFilteringProcessor;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.spi.BaseAttributeSource;
import org.jasig.services.persondir.spi.CriteriaSearchableAttributeSource;
import org.jasig.services.persondir.spi.cache.CacheKeyGenerator;

import com.google.common.base.Function;

public class CriteriaSearchableAttributeQueryWorker 
        extends AbstractAttributeQueryWorker<
            Criteria, 
            CriteriaSearchableAttributeSource, 
            CriteriaSearchableAttributeSourceConfig> {

    public CriteriaSearchableAttributeQueryWorker(
            PersonDirectoryConfig personDirectoryConfig,
            CriteriaSearchableAttributeSourceConfig sourceConfig,
            AttributeQuery<Criteria> attributeQuery,
            Queue<AttributeQueryWorker<?, ? extends AttributeSourceConfig<? extends BaseAttributeSource>>> completedWorkerQueue) {

        super(personDirectoryConfig, sourceConfig, attributeQuery, completedWorkerQueue);
    }


    public CriteriaSearchableAttributeQueryWorker(
            PersonDirectoryConfig personDirectoryConfig,
            CriteriaSearchableAttributeSourceConfig sourceConfig,
            PersonBuilder personBuilder,
            AttributeQuery<Criteria> attributeQuery,
            Queue<AttributeQueryWorker<?, ? extends AttributeSourceConfig<? extends BaseAttributeSource>>> completedWorkerQueue) {

        super(personDirectoryConfig, sourceConfig, personBuilder, attributeQuery, completedWorkerQueue);
    }


    @Override
    protected AttributeQueryTask createQueryCallable(AttributeQuery<Criteria> filteredQuery) {
        return new CriteriaSearchableAttributeQueryCallable(filteredQuery);
    }


    @Override
    protected Serializable generateCacheKey(AttributeQuery<Criteria> attributeQuery, CacheKeyGenerator keyGenerator) {
        return keyGenerator.generateCriteriaCacheKey(attributeQuery);
    }
    
    @Override
    protected Criteria filterQuery(Criteria q) {
        final CriteriaSearchableAttributeSourceConfig sourceConfig = getSourceConfig();
        final Set<String> requiredQueryAttributes = sourceConfig.getRequiredQueryAttributes();
        final Set<String> optionalQueryAttributes = sourceConfig.getOptionalQueryAttributes();
        
        final Map<String, Collection<String>> reverseAttributeMapping = sourceConfig.getReverseAttributeMapping();
        
        final CriteriaFilteringProcessor criteriaFilterProcessor = new CriteriaFilteringProcessor(new Function<String, String>() {
            public String apply(String attribute) {
                //Check if the source can actually handle this attribute
                if (!requiredQueryAttributes.contains(attribute) && !optionalQueryAttributes.contains(attribute)) {
                    return null;
                }
                
                //See if the attribute needs to get un-mapped
                final Collection<String> privateAttributes = reverseAttributeMapping.get(attribute);
                if (privateAttributes == null || privateAttributes.isEmpty()) {
                    return attribute;
                }

                //TODO what if there is more than one attribute?
                return privateAttributes.iterator().next();
            }
        });
        
        q.process(criteriaFilterProcessor);
        
        return criteriaFilterProcessor.getRootCriteria();
    }
    
    private final class CriteriaSearchableAttributeQueryCallable extends AttributeQueryTask {
        private final AttributeQuery<Criteria> attributeQuery;
        
        public CriteriaSearchableAttributeQueryCallable(AttributeQuery<Criteria> attributeQuery) {
            this.attributeQuery = attributeQuery;
        }

        @Override
        protected List<PersonAttributes> doQuery() {
            final CriteriaSearchableAttributeSource attributeSource = getSourceConfig().getAttributeSource();
            return attributeSource.searchForAttributes(this.attributeQuery);
        }
    }
}
