package org.jasig.services.persondir.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jasig.services.persondir.Person;
import org.jasig.services.persondir.PersonDirectory;
import org.jasig.services.persondir.core.config.AttributeSourceConfig;
import org.jasig.services.persondir.core.config.CriteriaSearchableAttributeSourceConfig;
import org.jasig.services.persondir.core.config.PersonDirectoryConfig;
import org.jasig.services.persondir.core.config.SimpleAttributeSourceConfig;
import org.jasig.services.persondir.core.config.SimpleSearchableAttributeSourceConfig;
import org.jasig.services.persondir.criteria.Criteria;
import org.springframework.core.OrderComparator;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Ordering;

public class PersonDirectoryImpl implements PersonDirectory {
    
    private final PersonDirectoryConfig config;
    private final List<CriteriaSearchableAttributeSourceConfig> criteriaSearchableSources;
    private final List<SimpleSearchableAttributeSourceConfig> simpleSearchableSources;
    private final List<SimpleAttributeSourceConfig> simpleSources;
    
    
    public PersonDirectoryImpl(PersonDirectoryConfig config) {
        this.config = config;
        
        final List<CriteriaSearchableAttributeSourceConfig> criteriaSearchableSourcesBuilder = new ArrayList<CriteriaSearchableAttributeSourceConfig>();
        final List<SimpleSearchableAttributeSourceConfig> simpleSearchableSourcesBuilder = new ArrayList<SimpleSearchableAttributeSourceConfig>();
        final List<SimpleAttributeSourceConfig> simpleSourcesBuilder = new ArrayList<SimpleAttributeSourceConfig>();

        for (final AttributeSourceConfig<?> sourceConfig : this.config.getSourceConfigs()) {
            //TODO register each attribute source config with jmx
            
            if (sourceConfig instanceof CriteriaSearchableAttributeSourceConfig) {
                criteriaSearchableSourcesBuilder.add((CriteriaSearchableAttributeSourceConfig)sourceConfig);
            }
            else if (sourceConfig instanceof SimpleSearchableAttributeSourceConfig) {
                simpleSearchableSourcesBuilder.add((SimpleSearchableAttributeSourceConfig)sourceConfig);
            }
            else if (sourceConfig instanceof SimpleAttributeSourceConfig) {
                simpleSourcesBuilder.add((SimpleAttributeSourceConfig)sourceConfig);
            }
            else {
                //TODO better exception
                throw new IllegalArgumentException("AttributeSourceConfig " + sourceConfig.getClass() + " does not implement a supported config interface.");
            }
        }
        
        //Create immutable copies sorted by their Order
        this.criteriaSearchableSources = Ordering.from(OrderComparator.INSTANCE).immutableSortedCopy(criteriaSearchableSourcesBuilder);
        this.simpleSearchableSources = Ordering.from(OrderComparator.INSTANCE).immutableSortedCopy(simpleSearchableSourcesBuilder);
        this.simpleSources = Ordering.from(OrderComparator.INSTANCE).immutableSortedCopy(simpleSourcesBuilder);
    }

    @Override
    public Person findPerson(String primaryId) {
        final Map<String, Object> attributes = ImmutableMap.<String, Object>of(config.getPrimaryIdAttribute(), primaryId);
        
        final List<Person> results = this.searchForPeople(attributes);
        
        if (results.isEmpty()) {
            return null;
        }
        
        if (results.size() > 1) {
            //TODO better exception
            throw new IllegalStateException(results.size() + " results were returned for findPerson(" + primaryId + "), 0 or 1 results was expected");
        }
        
        return results.get(0);
    }

    @Override
    public List<Person> searchForPeople(Map<String, Object> attributes) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Person> searchForPeople(Criteria query) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> getSearchableAttributeNames() {
        //TODO cache this for a short time?
        final Builder<String> attributeNamesBuilder = ImmutableSet.builder();
        
        for (final AttributeSourceConfig<?> sourceConfig : this.config.getSourceConfigs()) {
            final Set<String> requiredAttributes = sourceConfig.getRequiredAttributes();
            attributeNamesBuilder.addAll(requiredAttributes);
            
            final Set<String> optionalAttributes = sourceConfig.getOptionalAttributes();
            attributeNamesBuilder.addAll(optionalAttributes);
        }
        
        return attributeNamesBuilder.build();
    }

    @Override
    public Set<String> getAvailableAttributeNames() {
        //TODO cache this for a short time?
        final Builder<String> attributeNamesBuilder = ImmutableSet.builder();
        
        for (final AttributeSourceConfig<?> sourceConfig : this.config.getSourceConfigs()) {
            final Set<String> availableAttributes = sourceConfig.getAvailableAttributes();
            attributeNamesBuilder.addAll(availableAttributes);
        }
        
        return attributeNamesBuilder.build();
    }

}
