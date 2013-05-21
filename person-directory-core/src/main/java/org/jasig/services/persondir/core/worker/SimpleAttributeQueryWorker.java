package org.jasig.services.persondir.core.worker;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.core.PersonBuilder;
import org.jasig.services.persondir.core.config.AttributeSourceConfig;
import org.jasig.services.persondir.core.config.PersonDirectoryConfig;
import org.jasig.services.persondir.core.config.SimpleAttributeSourceConfig;
import org.jasig.services.persondir.core.criteria.CriteriaToMapFilteringProcessor;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.spi.BaseAttributeSource;
import org.jasig.services.persondir.spi.SimpleAttributeSource;
import org.jasig.services.persondir.spi.cache.CacheKeyGenerator;

import com.google.common.base.Function;

/**
 * Implementation for simple attribute queries
 * 
 * @author Eric Dalquist
 */
public class SimpleAttributeQueryWorker 
        extends AbstractAttributeQueryWorker<
            Map<String, Object>, 
            SimpleAttributeSource, 
            SimpleAttributeSourceConfig> {
    
    public SimpleAttributeQueryWorker(
            PersonDirectoryConfig personDirectoryConfig,
            SimpleAttributeSourceConfig sourceConfig,
            AttributeQuery<Criteria> attributeQuery,
            Queue<AttributeQueryWorker<?, ? extends AttributeSourceConfig<? extends BaseAttributeSource>>> completedWorkerQueue) {

        super(personDirectoryConfig, sourceConfig, attributeQuery, completedWorkerQueue);
    }

    public SimpleAttributeQueryWorker(
            PersonDirectoryConfig personDirectoryConfig,
            SimpleAttributeSourceConfig sourceConfig,
            PersonBuilder personBuilder,
            AttributeQuery<Criteria> attributeQuery,
            Queue<AttributeQueryWorker<?, ? extends AttributeSourceConfig<? extends BaseAttributeSource>>> completedWorkerQueue) {

        super(personDirectoryConfig, sourceConfig, personBuilder, attributeQuery, completedWorkerQueue);
    }

    /**
     * Call {@link CacheKeyGenerator#generateMapCacheKey(AttributeQuery)}
     */
    @Override
    protected Serializable generateCacheKey(AttributeQuery<Map<String, Object>> attributeQuery, CacheKeyGenerator keyGenerator) {
        return keyGenerator.generateMapCacheKey(attributeQuery);
    }

    /**
     * Convert a Critera into a query Map filtering out attributes that don't apply
     * to this attribute source
     */
    @Override
    protected Map<String, Object> filterQuery(Criteria criteria) {
        final SimpleAttributeSourceConfig sourceConfig = getSourceConfig();
        final Set<String> requiredQueryAttributes = sourceConfig.getRequiredQueryAttributes();
        final Set<String> optionalQueryAttributes = sourceConfig.getOptionalQueryAttributes();
        final Map<String, Collection<String>> reverseAttributeMapping = sourceConfig.getReverseAttributeMapping();
        
        final CriteriaToMapFilteringProcessor criteriaToMapProcessor = new CriteriaToMapFilteringProcessor(new Function<String, String>() {
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
        criteria.process(criteriaToMapProcessor);
        
        return criteriaToMapProcessor.getAttributes();
    }

    @Override
    protected AttributeQueryTask createQueryCallable(AttributeQuery<Map<String, Object>> filteredQuery) {
        return new SimpleAttributeQueryCallable(filteredQuery);
    }
    
    private final class SimpleAttributeQueryCallable extends AttributeQueryTask {
        private final AttributeQuery<Map<String, Object>> attributeQuery;
        
        public SimpleAttributeQueryCallable(AttributeQuery<Map<String, Object>> attributeQuery) {
            this.attributeQuery = attributeQuery;
        }

        @Override
        protected List<PersonAttributes> doQuery() {
            final SimpleAttributeSource attributeSource = getSourceConfig().getAttributeSource();
            final PersonAttributes result = attributeSource.findPersonAttributes(attributeQuery);
            return Collections.singletonList(result);
        }
    }
}
