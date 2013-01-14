package org.jasig.services.persondir.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.Person;
import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.PersonDirectory;
import org.jasig.services.persondir.core.config.AttributeSourceConfig;
import org.jasig.services.persondir.core.config.CriteriaSearchableAttributeSourceConfig;
import org.jasig.services.persondir.core.config.PersonDirectoryConfig;
import org.jasig.services.persondir.core.config.SimpleAttributeSourceConfig;
import org.jasig.services.persondir.core.config.SimpleSearchableAttributeSourceConfig;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.spi.gate.CriteriaSearchableAttributeSourceGate;
import org.jasig.services.persondir.spi.gate.SimpleAttributeSourceGate;
import org.jasig.services.persondir.spi.gate.SimpleSearchableAttributeSourceGate;
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
    private final List<AttributeSourceConfig<?, ?>> sortedSources;
//    private final List<CriteriaSearchableAttributeSourceConfig> criteriaSearchableSources;
//    private final List<SimpleSearchableAttributeSourceConfig> simpleSearchableSources;
//    private final List<SimpleAttributeSourceConfig> simpleSources;
    
    public PersonDirectoryImpl(PersonDirectoryConfig config) {
        this.config = config;
        
        final Set<AttributeSourceConfig<?, ?>> sourceConfigs = config.getSourceConfigs();
        
        //Sort the sources by type and order
        this.sortedSources = Ordering.from(AttributeSourceConfigComparator.INSTANCE).immutableSortedCopy(sourceConfigs);
        
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
        //TODO verify no null values
        
        //Map of results using the same ordering as the sources to make later merges easier
        final Map<AttributeSourceConfig<?, ?>, Future<List<PersonAttributes>>> resultFutures = new TreeMap<AttributeSourceConfig<?, ?>, Future<List<PersonAttributes>>>(AttributeSourceConfigComparator.INSTANCE);
        final Map<AttributeSourceConfig<?, ?>, List<PersonAttributes>> results = new TreeMap<AttributeSourceConfig<?, ?>, List<PersonAttributes>>(AttributeSourceConfigComparator.INSTANCE);
        
        /*
         * see BaseAdditiveAttributeMerger.mergeResults for ideas on how to merge to result lists
         *      SPI is string/obj but result is string/list<obj> - how do I deal with this missmatch?
         *      
         *      
         * generate a query map
         *      first pass just use the query
         *      later passes use the query + merge in results so far
         *          we run one query loop per result so far, not sure how this logic flows
         * set noNewQueriesRun = true
         * set noNewQueriesMatched = true
         * iterate through sources
         *  if source does not have an entry in sourceResults
         *      if source can be run based on current query map
         *          submit query to executor
         *          place Future in sourceResults map
         *          set noNewQueriesRun = false
         *          set noNewQueriesMatched = false
         * iterate through resultFutures
         *  if results.size + resultFutures.size == sources.size || noNewQueriesRun
         *      do future.get with "wait for timeout duration" flag
         *      set noNewQueriesRun = false if a result is retrieved from waiting for the future
         *  else
         *      do future.get with no waiting
         * if !resultFuture.isEmpty() || !noNewQueriesMatched || sources.size > results.size then repeat from start
         * merge results into final result map
         * 
         */

        boolean newQueryRan;
        do {
            final Map<String, Object> queryMap = query.getQuery();
            final Set<String> queryAttributes = queryMap.keySet();

            
            newQueryRan = false;
            //Run every source we are able to
            for (final AttributeSourceConfig<?, ?> sourceConfig : this.sortedSources) {
                if (resultFutures.containsKey(sourceConfig) || results.containsKey(sourceConfig)) {
                    //Skip any source that has already been processed
                    continue;
                }
                
                //determine if the source can be run for this query based on the attributes
                if (canQuerySource(sourceConfig, queryAttributes)) {
                    newQueryRan = true;
                    
                    final Future<List<PersonAttributes>> futureResult = doAttributesQuery(sourceConfig, queryMap);
                    resultFutures.put(sourceConfig, futureResult);
                }
            }
            
            //Check for results
            final int queryTimeout = query.getQueryTimeout();
            boolean retrievedResult = false;
            for (final Iterator<Map.Entry<AttributeSourceConfig<?, ?>, Future<List<PersonAttributes>>>> resultFuturesEntryItr = resultFutures.entrySet().iterator(); resultFuturesEntryItr.hasNext();) {
                final Map.Entry<AttributeSourceConfig<?, ?>, Future<List<PersonAttributes>>> resultFuturesEntry = resultFuturesEntryItr.next();
                
                final AttributeSourceConfig<?, ?> sourceConfig = resultFuturesEntry.getKey();
                try {
                    final List<PersonAttributes> result = getResult(resultFuturesEntry, queryTimeout, newQueryRan, retrievedResult);
                    
                    if (result != null) {
                        //non-null result means something was retrieved so remove the future
                        resultFuturesEntryItr.remove();
                        
                        //If there was actual result data set the retrievedResult flag so we know to try subsiquent queries
                        if (!result.isEmpty() && !retrievedResult) {
                            retrievedResult = true;
                        }
                        
                        //Put the result data in the results map
                        results.put(sourceConfig, result);
                    }
                } 
                catch (InterruptedException e) {
                    //ACK! We were interrupted, need to stop query execution ASAP and return
                    
                    //Mark this thread as interrupted
                    Thread.currentThread().interrupt();
                    
                    //Cancel all running futures
                    for (final Future<?> future : resultFutures.values()) {
                        future.cancel(true);
                    }
                    
                    //Return an empty result for the seach
                    return Collections.emptyList();
                } 
                catch (ExecutionException e) {
                    final Throwable cause = e.getCause();
                    this.logger.warn("Failed to execute " + query + " against " + sourceConfig, cause);
                    
                    final Ehcache errorCache = sourceConfig.getErrorCache();
                    if (errorCache != null) {
                        //TODO generate cache key, copy CacheKey from uPortal?
                        final Object cacheKey = null;
                        
                        errorCache.put(new Element(cacheKey, cause));
                    }
                } 
                catch (TimeoutException e) {
                    // TODO handling of hung PD query threads?
                    e.printStackTrace();
                }
            } 
            
        } while (
                !resultFutures.isEmpty() || //There are still futures that need results gotten from them
                (
                    newQueryRan && //A new query was run
                    sortedSources.size() > results.size() //And there are sources left to query
                )  
            );
        
        //TODO merge results goes here
        
        
        return null;
    }
    
    protected boolean canQuerySource(AttributeSourceConfig<?, ?> sourceConfig, AttributeQuery<?> attributeQuery) {
        final Set<String> requiredQueryAttributes = sourceConfig.getRequiredQueryAttributes();
        final Set<String> optionalQueryAttributes = sourceConfig.getOptionalQueryAttributes();
        
        if (    //If there are required attributes the query map must contain all of them
                (requiredQueryAttributes.isEmpty() || !queryAttributes.containsAll(requiredQueryAttributes))
                &&
                //If there are no required attributes the query map must contain at least one optional attribute
                (!requiredQueryAttributes.isEmpty() || Collections.disjoint(queryAttributes, optionalQueryAttributes))) {
            return false;
        }
        
        if (sourceConfig instanceof CriteriaSearchableAttributeSourceConfig) {
            for (final CriteriaSearchableAttributeSourceGate gate : ((CriteriaSearchableAttributeSourceConfig)sourceConfig).getGates()) {
                if (!gate.checkCriteriaSearch(query)) {
                    return false;
                }
            }
        }
        else if (sourceConfig instanceof SimpleSearchableAttributeSourceConfig) {
            for (final SimpleSearchableAttributeSourceGate gate : ((SimpleSearchableAttributeSourceConfig)sourceConfig).getGates()) {
                if (!gate.checkSimpleSearch(query)) {
                    return false;
                }
            }
        }
        else if (sourceConfig instanceof SimpleAttributeSourceConfig) {
            for (final SimpleAttributeSourceGate gate : ((SimpleAttributeSourceConfig)sourceConfig).getGates()) {
                if (!gate.checkFind(query)) {
                    return false;
                }
            }
        }
        else {
            throw new IllegalStateException(sourceConfig.getClass() + " is not a supported AttributeSourceConfig implementation");
        }
        
        return true;
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
        
        for (final AttributeSourceConfig<?, ?> sourceConfig : this.config.getSourceConfigs()) {
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
        
        for (final AttributeSourceConfig<?, ?> sourceConfig : this.config.getSourceConfigs()) {
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
