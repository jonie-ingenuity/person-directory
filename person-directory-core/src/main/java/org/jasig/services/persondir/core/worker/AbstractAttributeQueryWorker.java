package org.jasig.services.persondir.core.worker;

import java.io.Serializable;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.core.PersonBuilder;
import org.jasig.services.persondir.core.config.AttributeSourceConfig;
import org.jasig.services.persondir.core.config.PersonDirectoryConfig;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.spi.BaseAttributeSource;
import org.jasig.services.persondir.spi.cache.CacheKeyGenerator;
import org.springframework.util.Assert;

/**
 * Base query worker that encapsulates the lifecycle of executing an attribute query
 * 
 * @author Eric Dalquist
 * @param <Q> The generic type of the {@link AttributeQuery} 
 * @param <S> The {@link BaseAttributeSource} type
 * @param <C> The {@link AttributeSourceConfig} type
 */
public abstract class AbstractAttributeQueryWorker<
        Q,
        S extends BaseAttributeSource, 
        C extends AttributeSourceConfig<S>> implements AttributeQueryWorker<Q, C> {
    
    private final PersonDirectoryConfig personDirectoryConfig;
    private final C sourceConfig;
    private final AttributeQuery<Criteria> originalQuery;
    private final Queue<AttributeQueryWorker<?, ? extends AttributeSourceConfig<? extends BaseAttributeSource>>> completedWorkerQueue;
    private final PersonBuilder personBuilder;
    
    private long timeout;
    private AttributeQuery<Q> filteredQuery;
    
    private volatile Serializable cachedCacheKey;
    private Future<?> futureResult;
    private volatile List<PersonAttributes> result;
    private volatile Throwable error;
    
    private long submitted = 0;
    private volatile long started = 0;
    private volatile long complete = 0;

    /**
     * Initialize the query worker, all required pre-submit work happens here
     */
    public AbstractAttributeQueryWorker(
            PersonDirectoryConfig personDirectoryConfig,
            C sourceConfig,
            AttributeQuery<Criteria> attributeQuery,
            Queue<AttributeQueryWorker<?, ? extends AttributeSourceConfig<? extends BaseAttributeSource>>> completedWorkerQueue) {
        
        this(personDirectoryConfig, sourceConfig, null, attributeQuery, completedWorkerQueue);
    }
    
    public AbstractAttributeQueryWorker(
            PersonDirectoryConfig personDirectoryConfig,
            C sourceConfig,
            PersonBuilder personBuilder,
            AttributeQuery<Criteria> attributeQuery,
            Queue<AttributeQueryWorker<?, ? extends AttributeSourceConfig<? extends BaseAttributeSource>>> completedWorkerQueue) {
        
        Assert.notNull(personDirectoryConfig, "personDirectoryConfig cannot be null");
        Assert.notNull(sourceConfig, "sourceConfig cannot be null");
        Assert.notNull(attributeQuery, "attributeQuery cannot be null");
        Assert.notNull(completedWorkerQueue, "completedWorkerQueue cannot be null");
        
        this.personDirectoryConfig = personDirectoryConfig;
        this.sourceConfig = sourceConfig;
        this.personBuilder = personBuilder;
        this.completedWorkerQueue = completedWorkerQueue;
        
        this.originalQuery = attributeQuery;
        final Q query = filterQuery(attributeQuery.getQuery());
        this.filteredQuery = new AttributeQuery<Q>(query, attributeQuery);
        
        //Calculate the query timeout
        final int queryTimeout = this.filteredQuery.getQueryTimeout();
        if (queryTimeout > -1) {
            this.timeout = queryTimeout;
        }
        else {
            this.timeout = this.sourceConfig.getQueryTimeout();
        }
    }
    
    /**
     * Submit to the specified {@link ExecutorService} to execute
     * 
     * @throws IllegalStateException if {@link #submit(ExecutorService)} has already been called
     */
    @Override
    public final void submit(ExecutorService service) {
        Assert.notNull(service);
        if (this.futureResult != null) {
            throw new IllegalStateException("Cannot submit worker twice");
        }
        
        this.submitted = System.currentTimeMillis();
        
        //Check for a cached hit
        final Ehcache resultCache = sourceConfig.getResultCache();
        @SuppressWarnings("unchecked")
        final List<PersonAttributes> result = (List<PersonAttributes>)getCachedValue(resultCache);
        if (result != null) {
            final CachedAttributeQueryTask callable = new CachedAttributeQueryTask(result);
            callable.run();
            return;
        }
        
        //Check for a cached miss
        final Ehcache missCache = sourceConfig.getMissCache();
        @SuppressWarnings("unchecked")
        final List<PersonAttributes> miss = (List<PersonAttributes>)getCachedValue(missCache);
        if (miss != null) {
            final CachedAttributeQueryTask callable = new CachedAttributeQueryTask(miss);
            callable.run();
            return;
        }
        
        //Check for a cached error
        final Ehcache errorCache = sourceConfig.getErrorCache();
        final Throwable error = (Throwable)getCachedValue(errorCache);
        if (error != null) {
            final CachedAttributeQueryTask callable = new CachedAttributeQueryTask(error);
            callable.run();
            return;
        }
        
        //Nothing in the caches, submit the worker
        final AttributeQueryTask task = createQueryCallable(this.filteredQuery);
        this.futureResult = service.submit(task);
    }


    private void setResult(List<PersonAttributes> result) {
        this.result = result;
        
        //Empty result, cache it as a miss
        if (result.isEmpty()) {
            final Ehcache missCache = this.sourceConfig.getMissCache();
            if (missCache != null) {
                final Serializable cacheKey = this.getCacheKey();
                missCache.put(new Element(cacheKey, result));
            }
        }
        //Non-empty result, cacheit as a hit
        else {
            final Ehcache resultCache = this.sourceConfig.getResultCache();
            if (resultCache != null) {
                final Serializable cacheKey = getCacheKey();
                resultCache.put(new Element(cacheKey, result));
            }
        }
    }
    
    private void setError(Throwable t) {
        this.error = t;
        
        final Ehcache errorCache = this.sourceConfig.getErrorCache();
        if (errorCache != null) {
            final Serializable cacheKey = this.getCacheKey();
            errorCache.put(new Element(cacheKey, this.error));
        }
    }

    /**
     * Get the result of the attribute query, must be called after {@link #submit(ExecutorService)}
     * 
     * @throws InterruptedException if interrupted while waiting for the result
     * @throws IllegalStateException if {@link #submit(ExecutorService)} has not been called yet
     */
    @Override
    public final List<PersonAttributes> getResult() throws InterruptedException, TimeoutException {
        if (this.futureResult == null) {
            throw new IllegalStateException("submit must be called before getResult");
        }
        if (!this.futureResult.isDone()) {
            throw new IllegalStateException("getResult must not be called until the future has completed");
        }
        
        //Use already processed, throwable if it exists
        if (this.error != null) {
            rethrowUnchecked(this.error);
            //Return never actually executed but helps the compiler know that rethrowUnchecked always throws and this if block never completes
            return null;
        }
        
        return this.result;
    }

    /**
     * @return The number of milliseconds to wait for the result from this worker
     */
    @Override
    public final long getCurrentWaitTime() {
        return Math.max(1, this.timeout - (System.currentTimeMillis() - this.started));
    }
    
    /**
     * @return The filtered query used by the worker
     */
    @Override
    public final AttributeQuery<Q> getFilteredQuery() {
        return this.filteredQuery;
    }
    
    /**
     * @return The original query passed to the worker
     */
    @Override
    public final AttributeQuery<Criteria> getOriginalQuery() {
        return originalQuery;
    }

    /**
     * @return The time submitted, -1 if not yet submitted
     */
    @Override
    public final long getSubmitted() {
        return submitted;
    }

    /**
     * @return The time the query executed started, -1 if not yet started
     */
    @Override
    public final long getStarted() {
        return started;
    }

    /**
     * @return The time the query completed, -1 if not yet completed
     */
    @Override
    public final long getComplete() {
        return complete;
    }
    
    @Override
    public final PersonBuilder getPersonBuilder() {
        return personBuilder;
    }

    @Override
    public final boolean isComplete() {
        return complete != -1;
    }
    
    @Override
    public final boolean cancelFuture(boolean mayInterruptIfRunning) {
        return futureResult.cancel(mayInterruptIfRunning);
    }

    @Override
    public final boolean isFutureCancelled() {
        return futureResult.isCancelled();
    }

    @Override
    public final boolean isFutureDone() {
        return futureResult.isDone();
    }
    
    /**
     * @return The underlying attribute source configuration for this worker
     */
    @Override
    public final C getSourceConfig() {
        return sourceConfig;
    }
    
    /**
     * Utility to rethrow a genertic Throwable with minimal wrapping
     */
    protected final void rethrowUnchecked(Throwable t) {
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        throw new RuntimeException(t);
    }

    /**
     * @return Get the cache key to use for this query
     */
    protected final Serializable getCacheKey() {
        if (this.cachedCacheKey == null) {
            final CacheKeyGenerator cacheKeyGenerator = this.personDirectoryConfig.getCacheKeyGenerator();
            this.cachedCacheKey = generateCacheKey(this.filteredQuery, cacheKeyGenerator);
        }
        
        return this.cachedCacheKey;
    }
    
    /**
     * Get the cached value from the specified cache. Uses {@link #getCacheKey()} to
     * determine the key to use
     * 
     * @param cache The cache to get the data from
     * @return The data or null if the cache is null or no element was found
     */
    protected final Object getCachedValue(final Ehcache cache) {
        if (cache == null) {
            return null;
        }
        
        final Serializable cacheKey = getCacheKey();
        final Element resultElement = cache.get(cacheKey);
        if (resultElement == null) {
            return null;
        }
        
        return resultElement.getObjectValue();
    }
    
    /**
     * Create the {@link Callable} that does the actual query work, only called if no
     * cached result exists
     */
    protected abstract AttributeQueryTask createQueryCallable(AttributeQuery<Q> filteredQuery);
    
    /**
     * Generate the cache key for the query, only called if caches are configured
     */
    protected abstract Serializable generateCacheKey(AttributeQuery<Q> attributeQuery, CacheKeyGenerator keyGenerator);
    
    /**
     * Filter the query for this attribute source, called during construction
     */
    protected abstract Q filterQuery(Criteria criteria);

    /**
     * Base {@link Callable} that does the query work, tracks start/stop time for the query
     */
    protected abstract class AttributeQueryTask implements Runnable {
        @Override
        public final void run() {
            started = System.currentTimeMillis();
            try {
                final List<PersonAttributes> result = doQuery();
                setResult(result);
            }
            catch (Throwable t) {
                setError(t);
            }
            finally {
                complete = System.currentTimeMillis();
                completedWorkerQueue.offer(AbstractAttributeQueryWorker.this);
            }
        }
        
        /**
         * @return Execute the query and return the value
         */
        protected abstract List<PersonAttributes> doQuery();
    }
    
    private class CachedAttributeQueryTask extends AttributeQueryTask {
        private final List<PersonAttributes> result;
        private final Throwable error;
        
        public CachedAttributeQueryTask(Throwable error) {
            this.result = null;
            this.error = error;
        }
        public CachedAttributeQueryTask(List<PersonAttributes> result) {
            this.result = result;
            this.error = null;
        }

        @Override
        protected List<PersonAttributes> doQuery() {
            if (this.error != null) {
                rethrowUnchecked(error);
            }
            return this.result;
        }
    }
}
