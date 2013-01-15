package org.jasig.services.persondir.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
import org.jasig.services.persondir.spi.BaseAttributeSource;
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
    
    /**
     * Callback on worker completion that places the worker on the completeWorkerQueue
     */
    private static final class WorkerCompleteCallback implements Runnable {
        private final CriteriaSearchableAttributeQueryWorker criteriaSearchableQueryWorker;
        private final Queue<AbstractAttributeQueryWorker<?, ? extends BaseAttributeSource, ? extends AttributeSourceConfig<? extends BaseAttributeSource>>> completeWorkerQueue;

        private WorkerCompleteCallback(
                CriteriaSearchableAttributeQueryWorker criteriaSearchableQueryWorker,
                Queue<AbstractAttributeQueryWorker<?, ? extends BaseAttributeSource, ? extends AttributeSourceConfig<? extends BaseAttributeSource>>> completeWorkerQueue) {

            this.criteriaSearchableQueryWorker = criteriaSearchableQueryWorker;
            this.completeWorkerQueue = completeWorkerQueue;
        }
        
        @Override
        public void run() {
            completeWorkerQueue.add(criteriaSearchableQueryWorker);
        }
    }


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
         * 
         * TODO where to put attribute transformations
         * TODO delete merge order/behavior
         * 
         */
        final Criteria originalCriteria = attributeQuery.getQuery();
        
        //Structures to track attribute query worker progress
        final Map<AttributeSourceConfig<?>, AbstractAttributeQueryWorker<?, ? extends BaseAttributeSource, ? extends AttributeSourceConfig<? extends BaseAttributeSource>>> 
            runningWorkers = new HashMap<AttributeSourceConfig<?>, AbstractAttributeQueryWorker<?, ? extends BaseAttributeSource, ? extends AttributeSourceConfig<? extends BaseAttributeSource>>>();
        
        final Map<AttributeSourceConfig<?>, AbstractAttributeQueryWorker<?, ? extends BaseAttributeSource, ? extends AttributeSourceConfig<? extends BaseAttributeSource>>> 
            completeWorkers = new HashMap<AttributeSourceConfig<?>, AbstractAttributeQueryWorker<?, ? extends BaseAttributeSource, ? extends AttributeSourceConfig<? extends BaseAttributeSource>>>();
        
        final BlockingQueue<AbstractAttributeQueryWorker<?, ? extends BaseAttributeSource, ? extends AttributeSourceConfig<? extends BaseAttributeSource>>> 
            completeWorkerQueue = new LinkedBlockingQueue<AbstractAttributeQueryWorker<?, ? extends BaseAttributeSource, ? extends AttributeSourceConfig<? extends BaseAttributeSource>>>();
        
        //First Pass: runs the Criteria against any DAO that it matches against
        final Set<AttributeSourceConfig<?>> sourceConfigs = config.getSourceConfigs();
        for (final AttributeSourceConfig<?> sourceConfig : sourceConfigs) {
            if (sourceConfig instanceof CriteriaSearchableAttributeSourceConfig) {
                final CriteriaSearchableAttributeSourceConfig criteriaSearchableSourceConfig = (CriteriaSearchableAttributeSourceConfig)sourceConfig;
                if (canRun(originalCriteria, criteriaSearchableSourceConfig)) {
                    final CriteriaSearchableAttributeQueryWorker criteriaSearchableQueryWorker = new CriteriaSearchableAttributeQueryWorker(this.config, criteriaSearchableSourceConfig, attributeQuery);
                    runningWorkers.put(sourceConfig, criteriaSearchableQueryWorker);
                    
                    final WorkerCompleteCallback futureCallback = new WorkerCompleteCallback(criteriaSearchableQueryWorker, completeWorkerQueue);
                    criteriaSearchableQueryWorker.submit(attributeSourceExecutor, futureCallback);
                }
            }
        }
        
        final Map<String, PersonAttributes> resultsMap = new LinkedHashMap<String, PersonAttributes>();
        
        //2..N Pass: uses each result as a 
        boolean resultHaveChanged = false;
        do {
            if (resultHaveChanged) {
                //do pass 2..N queries on remaining sources
            }
            
            //Reset resultsHaveChanged flag
            resultHaveChanged = false;
            
            //Calculate the maximum time to wait for a result based on the currently executing workers
            long maxWaitTime = 0;
            for (final AbstractAttributeQueryWorker<?, ? extends BaseAttributeSource, ? extends AttributeSourceConfig<? extends BaseAttributeSource>> queryWorker : runningWorkers.values()) {
                maxWaitTime = Math.max(maxWaitTime, queryWorker.getCurrentWaitTime());
            }
            
            //Wait for a result to appear in the completeWorkerQueue
            AbstractAttributeQueryWorker<?, ? extends BaseAttributeSource, ? extends AttributeSourceConfig<? extends BaseAttributeSource>> completeWorker = null;
            try {
                completeWorker = completeWorkerQueue.poll(maxWaitTime, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                //Interrupted by something while waiting for a result, assume we need to abort blocking operations and just return
                //mark the thread as interrupted and break out of the wait-for-results loop
                Thread.currentThread().interrupt();
                break;
            }
            
            if (completeWorker == null) {
                // uhoh everything in-progress took longer than it should have and we can't have any new results at this point
                // Cancel all in-progress workers and break out of the loop
                for (final AbstractAttributeQueryWorker<?, ? extends BaseAttributeSource, ? extends AttributeSourceConfig<? extends BaseAttributeSource>> queryWorker : runningWorkers.values()) {
                    queryWorker.cancel(true);
                    break;
                }
            }
            else {
                while (completeWorker != null) {
                    //Move the worker from the running map into the complete map
                    final AttributeSourceConfig<? extends BaseAttributeSource> sourceConfig = completeWorker.getSourceConfig();
                    runningWorkers.remove(sourceConfig);
                    completeWorkers.put(sourceConfig, completeWorker);
                    
                    try {
                        final List<PersonAttributes> results = completeWorker.getResult();
                        addResults(resultsMap, results);
                    }
                    catch (Throwable t) {
                        logger.warn("'" + sourceConfig.getName() + "' threw an exception while retrieving attributes and will be ignored for query: " + completeWorker.getFilteredQuery(), t);
                    }
                 
                    //Check if there are any more completed workers without waiting
                    completeWorker = completeWorkerQueue.poll();
                }
            }
        } while (sourceConfigs.size() < completeWorkers.size() && resultHaveChanged);
        
        
        /* results processing
         *  apply original critera to each result
         *  convert each PersonAttributes to a Person
         *  build into ImmutableList
         * 
         */
        
        return null;
    }
    
    protected void addResults(Map<String, PersonAttributes> resultsMap, List<PersonAttributes> results) {
        //TODO
    }
    
    protected boolean canRun(Criteria criteria, CriteriaSearchableAttributeSourceConfig sourceConfig) {
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
    
    protected boolean canRun(Map<String, Object> attributes, SimpleAttributeSourceConfig sourceConfig) {
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
