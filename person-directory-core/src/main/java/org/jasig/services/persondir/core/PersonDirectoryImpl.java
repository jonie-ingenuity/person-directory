package org.jasig.services.persondir.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
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
import org.jasig.services.persondir.core.worker.AbstractAttributeQueryWorker;
import org.jasig.services.persondir.core.worker.CriteriaSearchableAttributeQueryWorker;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.criteria.CriteriaBuilder;
import org.jasig.services.persondir.util.criteria.CriteriaAttributeNamesHandler;
import org.jasig.services.persondir.util.criteria.CriteriaWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class PersonDirectoryImpl implements PersonDirectory {
    protected final Logger logger = LoggerFactory.getLogger(getClass()); 
    
    protected final PersonDirectoryConfig config;
    private final ListeningExecutorService attributeSourceExecutor;

    public PersonDirectoryImpl(PersonDirectoryConfig config) {
        this.config = config;
        
        //Setup executor service
        final ExecutorService executorService = config.getExecutorService();
        if (executorService == null) {
            logger.warn("No ExecutorService was configured, all attribute retrieval will be done serially.");
            this.attributeSourceExecutor = MoreExecutors.sameThreadExecutor();
        }
        else {
            this.attributeSourceExecutor = MoreExecutors.listeningDecorator(executorService);
        }
    }

    @Override
    public Person findPerson(String primaryId) {
        final Criteria criteria = CriteriaBuilder.eq(this.config.getPrimaryIdAttribute(), primaryId);
        final AttributeQuery<Criteria> attributeQuery = this.createDefaultQuery(criteria);
        
        final List<Person> results = this.searchForPeople(attributeQuery);
        
        if (results.isEmpty()) {
            return null;
        }
        if (results.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(results.size() + " results were returned for findPerson(" + primaryId + "), 0 or 1 results was expected", 1, results.size());
        }
        
        return results.get(0);
    }
    
    @Override
    public List<Person> searchForPeople(Criteria query) {
        return searchForPeople(createDefaultQuery(query));
    }
    @Override
    public List<Person> searchForPeople(AttributeQuery<Criteria> attributeQuery) {
        /*
         * see BaseAdditiveAttributeMerger.mergeResults for ideas on how to merge to result lists
         *      SPI is string/obj but result is string/list<obj> - how do I deal with this missmatch?
         *      
         * pass 1 uses original Criteria against all DAOs that it can match against
         *  extra attributes in the critera are filtered out for that dao
         * pass 2..N treats each result in the result set as a big OR criteria
         * after no more sources or no new result mods the final result list is filtered by applying the original Criteria
         * 
         * problems with this ... using OR for the subsequent queries would result in exponential growth
         *  we are looking for additional data for a single user so the attributes in the sub-queries should use AND
         *
         * Do we need a Map dao? yes for single result sources that have no concept of "searching"
         * 
         * For work execution use a ListenableFuture with a MoreExecutors.sameThreadExecutor() to push the result
         * onto a concurrent queue. The main thread can then simply wait on the concurrent queue for new results
         * to show up
         * 
         */
        final Criteria originalCriteria = attributeQuery.getQuery();
        
        final Map<AttributeSourceConfig<?>, AbstractAttributeQueryWorker<?, ?, ?>> workers = new HashMap<AttributeSourceConfig<?>, AbstractAttributeQueryWorker<?,?,?>>();
        final Queue<AbstractAttributeQueryWorker<?, ?, ?>> resultQueue = new ConcurrentLinkedQueue<AbstractAttributeQueryWorker<?,?,?>>();
        
        //First pass, runs the Criteria against any DAO that it matches against
        for (final AttributeSourceConfig<?> sourceConfig : config.getSourceConfigs()) {
            if (sourceConfig instanceof CriteriaSearchableAttributeSourceConfig) {
                final CriteriaSearchableAttributeSourceConfig criteriaSearchableSourceConfig = (CriteriaSearchableAttributeSourceConfig)sourceConfig;
                if (canRun(originalCriteria, criteriaSearchableSourceConfig)) {
                    final CriteriaSearchableAttributeQueryWorker criteriaSearchableQueryWorker = new CriteriaSearchableAttributeQueryWorker(this.config, criteriaSearchableSourceConfig, attributeQuery);
                    criteriaSearchableQueryWorker.submit(attributeSourceExecutor);
                }
            }
        }

        boolean newQueryRan;
        do {
            final Map<String, Object> queryMap = query.getCriteria();
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
    
    public boolean canRun(Criteria criteria, CriteriaSearchableAttributeSourceConfig sourceConfig) {
        final CriteriaAttributeNamesHandler criteriaAttributeNamesHandler = new CriteriaAttributeNamesHandler();
        CriteriaWalker.walkCriteria(criteria, criteriaAttributeNamesHandler);
        
        final Set<String> queryAttributeNames = criteriaAttributeNamesHandler.getAttributeNames();
        
        final Set<String> requiredQueryAttributes = sourceConfig.getRequiredQueryAttributes();
        final Set<String> optionalQueryAttributes = sourceConfig.getOptionalQueryAttributes();
        if (    //If there are required attributes the query map must contain all of them
                (requiredQueryAttributes.isEmpty() || !queryAttributeNames.containsAll(requiredQueryAttributes))
                &&
                //If there are no required attributes the query map must contain at least one optional attribute
                (!requiredQueryAttributes.isEmpty() || Collections.disjoint(queryAttributeNames, optionalQueryAttributes))) {
            return false;
        }
        
        //TODO gates
        
        return true;
    }
    
    public boolean canRun(Map<String, Object> attributes, SimpleAttributeSourceConfig sourceConfig) {
        final Set<String> queryAttributeNames = attributes.keySet();
        
        final Set<String> requiredQueryAttributes = sourceConfig.getRequiredQueryAttributes();
        final Set<String> optionalQueryAttributes = sourceConfig.getOptionalQueryAttributes();
        if (    //If there are required attributes the query map must contain all of them
                (requiredQueryAttributes.isEmpty() || !queryAttributeNames.containsAll(requiredQueryAttributes))
                &&
                //If there are no required attributes the query map must contain at least one optional attribute
                (!requiredQueryAttributes.isEmpty() || Collections.disjoint(queryAttributeNames, optionalQueryAttributes))) {
            return false;
        }
        
        //TODO gate check
        
        return true;
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
    protected AttributeQuery<Criteria> createDefaultQuery(Criteria criteria) {
        return new AttributeQuery<Criteria>(criteria, config.getDefaultMaxResults(), config.getDefaultQueryTimeout());
    }
}
