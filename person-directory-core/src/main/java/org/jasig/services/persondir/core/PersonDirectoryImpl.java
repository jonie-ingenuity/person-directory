package org.jasig.services.persondir.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.Person;
import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.PersonDirectory;
import org.jasig.services.persondir.core.config.AttributeSourceConfig;
import org.jasig.services.persondir.core.config.PersonDirectoryConfig;
import org.jasig.services.persondir.criteria.Criteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Ordering;
import com.google.common.util.concurrent.MoreExecutors;

public class PersonDirectoryImpl implements PersonDirectory {
    protected final Logger logger = LoggerFactory.getLogger(getClass()); 
    
    protected final PersonDirectoryConfig config;
    private final ExecutorService attributeSourceExecutor;
    private final List<AttributeSourceConfig<?>> sortedSources;
//    private final List<CriteriaSearchableAttributeSourceConfig> criteriaSearchableSources;
//    private final List<SimpleSearchableAttributeSourceConfig> simpleSearchableSources;
//    private final List<SimpleAttributeSourceConfig> simpleSources;
    
    public PersonDirectoryImpl(PersonDirectoryConfig config) {
        this.config = config;
        
        final Set<AttributeSourceConfig<?>> sourceConfigs = config.getSourceConfigs();
        
        //Sort the sources by type and order
        this.sortedSources = Ordering.from(AttributeSourceComparator.INSTANCE).immutableSortedCopy(sourceConfigs);
        
//        final List<CriteriaSearchableAttributeSourceConfig> criteriaSearchableSourcesBuilder = new ArrayList<CriteriaSearchableAttributeSourceConfig>();
//        final List<SimpleSearchableAttributeSourceConfig> simpleSearchableSourcesBuilder = new ArrayList<SimpleSearchableAttributeSourceConfig>();
//        final List<SimpleAttributeSourceConfig> simpleSourcesBuilder = new ArrayList<SimpleAttributeSourceConfig>();
//
//        for (final AttributeSourceConfig<?> sourceConfig : this.config.getSourceConfigs()) {
//            //TODO register each attribute source config with jmx
//            
//            if (sourceConfig instanceof CriteriaSearchableAttributeSourceConfig) {
//                criteriaSearchableSourcesBuilder.add((CriteriaSearchableAttributeSourceConfig)sourceConfig);
//            }
//            else if (sourceConfig instanceof SimpleSearchableAttributeSourceConfig) {
//                simpleSearchableSourcesBuilder.add((SimpleSearchableAttributeSourceConfig)sourceConfig);
//            }
//            else if (sourceConfig instanceof SimpleAttributeSourceConfig) {
//                simpleSourcesBuilder.add((SimpleAttributeSourceConfig)sourceConfig);
//            }
//            else {
//                //TODO better exception
//                throw new IllegalArgumentException("AttributeSourceConfig " + sourceConfig.getClass() + " does not implement a supported config interface.");
//            }
//        }
//        
//        //Create immutable copies sorted by their Order
//        this.criteriaSearchableSources = Ordering.from(OrderComparator.INSTANCE).immutableSortedCopy(criteriaSearchableSourcesBuilder);
//        this.simpleSearchableSources = Ordering.from(OrderComparator.INSTANCE).immutableSortedCopy(simpleSearchableSourcesBuilder);
//        this.simpleSources = Ordering.from(OrderComparator.INSTANCE).immutableSortedCopy(simpleSourcesBuilder);
        
        //Setup executor service
        final ExecutorService executorService = config.getExecutorService();
        if (executorService == null) {
            logger.warn("No ExecutorService was configured, all attribute retrieval will be done serially.");
            this.attributeSourceExecutor = MoreExecutors.sameThreadExecutor();
        }
        else {
            this.attributeSourceExecutor = executorService;
        }
    }

    @Override
    public Person findPerson(String primaryId) {
        return this.findPerson(createDefaultQuery(primaryId));
    }
    @Override
    public Person findPerson(AttributeQuery<String> query) {
        final String primaryId = query.getQuery();
        final Map<String, Object> attributes = ImmutableMap.<String, Object>of(config.getPrimaryIdAttribute(), primaryId);
        
        final AttributeQuery<Map<String, Object>> subQuery = new AttributeQuery<Map<String, Object>>(attributes, query.getMaxResults(), query.getQueryTimeout());
        final List<Person> results = this.simpleSearchForPeople(subQuery);
        
        if (results.isEmpty()) {
            return null;
        }
        if (results.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(results.size() + " results were returned for findPerson(" + primaryId + "), 0 or 1 results was expected", 1, results.size());
        }
        
        return results.get(0);
    }

    @Override
    public List<Person> simpleSearchForPeople(Map<String, Object> attributes) {
        return simpleSearchForPeople(createDefaultQuery(attributes));
    }
    @Override
    public List<Person> simpleSearchForPeople(AttributeQuery<Map<String, Object>> query) {
        final List<AttributeSourceConfig<?>> sources = new ArrayList<AttributeSourceConfig<?>>(this.sortedSources);
        final List<Future<List<PersonAttributes>>> sourceQueryFutures = new ArrayList<Future<List<PersonAttributes>>>(sources.size());
        
        final List<List<PersonAttributes>> results = new LinkedList<List<PersonAttributes>>();
        
        final Map<String, Object> queryMap = query.getQuery();
        final Set<String> queryAttributes = queryMap.keySet();
        
        
        while (!sources.isEmpty()) {
            //TODO probably need state tracking booleans here like ranQuery and gotResult
            
            //Run every source we are able to
            for (final Iterator<AttributeSourceConfig<?>> sourceItr = sources.iterator(); sourceItr.hasNext();) {
                final AttributeSourceConfig<?> source = sourceItr.next();
                
                //determine is source can be run for this query
                final Set<String> requiredQueryAttributes = source.getRequiredQueryAttributes();
                final Set<String> optionalQueryAttributes = source.getOptionalQueryAttributes();
                
                if (
                    //If there are required attributes the query map must contain all of them
                    (!requiredQueryAttributes.isEmpty() && queryAttributes.containsAll(requiredQueryAttributes)) ||
                    //If there are no required attributes the query map must contain at least one optional attribute
                    (requiredQueryAttributes.isEmpty() && !Collections.disjoint(queryAttributes, optionalQueryAttributes))) {
                    
                    sourceItr.remove();
                }
                
                /*
                 * if this source can be run for this query
                 *  remove from itr
                 *  check hit, miss, error caches
                 *  submit query to exec
                 *      run query filters
                 *      run query
                 */
                
//                if (source instanceof CriteriaSearchableAttributeSourceConfig) {
//                    final CriteriaSearchableAttributeSource attributeSource = ((CriteriaSearchableAttributeSourceConfig)source).getAttributeSource();
//                }
            }
            
            //Try getting all results without waiting, if at least one result is returned without a wait then repeat while loop
            //If no results are returned without waiting then try again but wait on the first future
            //TODO wait time needs to take into account time since submission?
            for (final Iterator<Future<List<PersonAttributes>>> sourceFutureItr = sourceQueryFutures.iterator(); sourceFutureItr.hasNext();) {
                final Future<List<PersonAttributes>> sourceFuture = sourceFutureItr.next();
                
                if (sourceFuture.isDone()) {
                    try {
                        final List<PersonAttributes> result = sourceFuture.get();
                    }
                    catch (Exception e) {
                        //
                    }
                }
            }
            
            /*
             * If we run through an iteration of all remaining sources and none can be queried break the outer while loop, the remaining sources cannot be queried 
             * TODO source config option to warn/info/debug log a source that is skipped for lack of matching query data
             */
        }
        
        return null;
    }

    @Override
    public List<Person> criteriaSearchForPeople(Criteria query) {
        return criteriaSearchForPeople(createDefaultQuery(query));
    }
    @Override
    public List<Person> criteriaSearchForPeople(AttributeQuery<Criteria> query) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> getSearchableAttributeNames() {
        //TODO cache this for a short time?
        final Builder<String> attributeNamesBuilder = ImmutableSet.builder();
        
        for (final AttributeSourceConfig<?> sourceConfig : this.config.getSourceConfigs()) {
            final Set<String> requiredAttributes = sourceConfig.getRequiredQueryAttributes();
            attributeNamesBuilder.addAll(requiredAttributes);
            
            final Set<String> optionalAttributes = sourceConfig.getOptionalQueryAttributes();
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

    
    /**
     * Create an {@link AttributeQuery} using the default configuration, wrapping the specified query.
     */
    protected <Q> AttributeQuery<Q> createDefaultQuery(Q query) {
        return new AttributeQuery<Q>(query, config.getDefaultMaxResults(), config.getDefaultQueryTimeout());
    }
}
