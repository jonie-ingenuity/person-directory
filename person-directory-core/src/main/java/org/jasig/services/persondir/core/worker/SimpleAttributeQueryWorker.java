package org.jasig.services.persondir.core.worker;

import java.io.Serializable;
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

    @Override
    protected Serializable generateCacheKey(AttributeQuery<Map<String, Object>> attributeQuery, CacheKeyGenerator keyGenerator) {
        return keyGenerator.generateMapCacheKey(attributeQuery);
    }

    @Override
    protected Map<String, Object> filterQuery(Criteria criteria) {
        final SimpleAttributeSourceConfig sourceConfig = getSourceConfig();
        final Set<String> requiredQueryAttributes = sourceConfig.getRequiredQueryAttributes();
        final Set<String> optionalQueryAttributes = sourceConfig.getOptionalQueryAttributes();
        
        final CriteriaToMapFilteringProcessor criteriaToMapProcessor = new CriteriaToMapFilteringProcessor(new Function<String, Boolean>() {
            public Boolean apply(String attribute) {
                return requiredQueryAttributes.contains(attribute) || optionalQueryAttributes.contains(attribute);
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
