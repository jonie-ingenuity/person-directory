package org.jasig.services.persondir.core;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.core.config.AttributeSourceConfig;
import org.jasig.services.persondir.core.config.PersonDirectoryConfig;
import org.jasig.services.persondir.spi.BaseAttributeSource;
import org.jasig.services.persondir.spi.cache.CacheKeyGenerator;
import org.springframework.util.Assert;

import com.google.common.util.concurrent.Futures;

/**
 * Base query worker that encapsulates the lifecycle of executing an attribute query
 * 
 * @author Eric Dalquist
 * @param <Q> The generic type of the {@link AttributeQuery} 
 * @param <S> The {@link BaseAttributeSource} type
 * @param <C> The {@link AttributeSourceConfig} type
 */
abstract class AbstractAttributeQueryWorker<
        Q, 
        S extends BaseAttributeSource, 
        C extends AttributeSourceConfig<S, ?>> {
    
    protected final PersonDirectoryConfig personDirectoryConfig;
    protected final C sourceConfig;
    protected final AttributeQuery<Q> filteredQuery;
    protected final long timeout;
    
    private Serializable cachedCacheKey;
    private Future<List<PersonAttributes>> futureResult;
    private List<PersonAttributes> result;
    private Throwable error;
    
    private long submitted = 0;
    private volatile long started = 0;
    private volatile long complete = 0;

    /**
     * Initialize the query worker, all required pre-submit work happens here
     */
    public AbstractAttributeQueryWorker(
            PersonDirectoryConfig personDirectoryConfig,
            C sourceConfig,
            AttributeQuery<Q> originalQuery) {
        
        Assert.notNull(personDirectoryConfig, "personDirectoryConfig cannot be null");
        Assert.notNull(sourceConfig, "sourceConfig cannot be null");
        Assert.notNull(originalQuery, "originalQuery cannot be null");

        this.personDirectoryConfig = personDirectoryConfig;
        this.sourceConfig = sourceConfig;
        
        //Filter the original query into a query specifically for this attribute source
        final Q query = filterQuery(originalQuery.getQuery());
        this.filteredQuery = new AttributeQuery<Q>(query, originalQuery);
        
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
            this.started = this.submitted;
            this.complete = this.started;
            this.futureResult = Futures.immediateFuture(result);
            return;
        }
        
        //Check for a cached miss
        final Ehcache missCache = sourceConfig.getMissCache();
        @SuppressWarnings("unchecked")
        final List<PersonAttributes> miss = (List<PersonAttributes>)getCachedValue(missCache);
        if (miss != null) {
            this.started = this.submitted;
            this.complete = this.started;
            this.futureResult = Futures.immediateFuture(miss);
            return;
        }
        
        //Check for a cached error
        final Ehcache errorCache = sourceConfig.getErrorCache();
        final Throwable error = (Throwable)getCachedValue(errorCache);
        if (error != null) {
            this.started = this.submitted;
            this.complete = this.started;
            this.futureResult = Futures.immediateFailedFuture(error);
            return;
        }
        
        //Nothing in the caches, submit the worker
        final AttributeQueryCallable task = createQueryCallable(filteredQuery);
        this.futureResult = service.submit(task);
    }
    
    /**
     * Get the result of the attribute query, must be called after {@link #submit(ExecutorService)}
     * 
     * @throws InterruptedException if interrupted while waiting for the result
     * @throws IllegalStateException if {@link #submit(ExecutorService)} has not been called yet
     */
    public final List<PersonAttributes> getResult(boolean wait) throws InterruptedException, TimeoutException {
        if (this.futureResult == null) {
            throw new IllegalStateException("submit must be called before getResult");
        }
        
        //Use already processed result if it exists
        if (this.result != null) {
            return this.result;
        }
        //Use already processed, throwable if it exists
        if (this.error != null) {
            rethrowUnchecked(this.error);
            //Return never actually executed but helps the compiler know that rethrowUnchecked always throws and this if block never completes
            return null;
        }
        
        try {
            if (wait) {
                //If a timeout is set calculate the wait time based on when the query was started, wait a minimum of 1ms
                if (this.timeout > -1) {
                    final long waitTime = Math.max(1, this.timeout - (System.currentTimeMillis() - this.started));
                    this.result = this.futureResult.get(waitTime, TimeUnit.MILLISECONDS);
                }
                //If no timeout is set wait forever
                else {
                    this.result = this.futureResult.get();
                }
            }
            else if (this.futureResult.isDone()) {
                //No waiting and future is done so just get the result
                this.result = this.futureResult.get();
            }
            else { 
                //Not waiting and future isn't done, return null
                return null;
            }
        }
        catch (InterruptedException e) {
            //ACK we were interrupted while waiting, cancel the future, interrupting it, and rethrow
            this.futureResult.cancel(true);
            throw e;
        }
        catch (ExecutionException e) {
            //Attribute source threw an exception, cache it and rethrow
            this.error = e.getCause();
            
            handleResultException();
            //Return never actually executed but helps the compiler know that handleResultException always throws and this if block never completes
            return null;
        } 
        catch (TimeoutException e) {
            //Timed out waiting for the source to return, cache it and rethrow
            this.error = e;
            
            handleResultException();
            //Return never actually executed but helps the compiler know that handleResultException always throws and this if block never completes
            return null;
        }
        
        //Empty result, cache it as a miss
        if (result.isEmpty()) {
            final Ehcache missCache = sourceConfig.getMissCache();
            if (missCache != null) {
                final Serializable cacheKey = this.getCacheKey();
                missCache.put(new Element(cacheKey, result));
            }
        }
        //Non-empty result, cacheit as a hit
        else {
            final Ehcache resultCache = sourceConfig.getResultCache();
            if (resultCache != null) {
                final Serializable cacheKey = this.getCacheKey();
                resultCache.put(new Element(cacheKey, result));
            }
        }
        
        //Return the result
        return result;
    }
    
    /**
     * @return The time submitted, -1 if not yet submitted
     */
    public final long getSubmitted() {
        return submitted;
    }

    /**
     * @return The time the query executed started, -1 if not yet started
     */
    public final long getStarted() {
        return started;
    }

    /**
     * @return The time the query completed, -1 if not yet completed
     */
    public final long getComplete() {
        return complete;
    }

    /**
     * Common exception handling code for getting the result from the future
     */
    protected final void handleResultException() throws TimeoutException {
        final Ehcache errorCache = sourceConfig.getErrorCache();
        if (errorCache != null) {
            final Serializable cacheKey = this.getCacheKey();
            errorCache.put(new Element(cacheKey, this.error));
        }
        
        rethrowUnchecked(this.error);
    }
    
    /**
     * Utility to rethrow a genertic Throwable with minimal wrapping
     */
    protected final void rethrowUnchecked(Throwable t) throws TimeoutException {
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        if (t instanceof TimeoutException) {
            throw (TimeoutException)t;
        }
        throw new RuntimeException(t);
    }

    /**
     * @return Get the cache key to use for this query
     */
    protected final Serializable getCacheKey() {
        if (this.cachedCacheKey == null) {
            final CacheKeyGenerator cacheKeyGenerator = this.personDirectoryConfig.getCacheKeyGenerator();
            final Q c = this.filteredQuery.getQuery();
            this.cachedCacheKey = generateCacheKey(c, cacheKeyGenerator);
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
    protected abstract AttributeQueryCallable createQueryCallable(AttributeQuery<Q> filteredQuery);
    
    /**
     * Generate the cache key for the query, only called if caches are configured
     */
    protected abstract Serializable generateCacheKey(Q q, CacheKeyGenerator keyGenerator);
    
    /**
     * Filter the query for this attribute source, called during construction
     */
    protected abstract Q filterQuery(Q q);

    /**
     * Base {@link Callable} that does the query work, tracks start/stop time for the query
     */
    protected abstract class AttributeQueryCallable implements Callable<List<PersonAttributes>> {
        @Override
        public final List<PersonAttributes> call() throws Exception {
            started = System.currentTimeMillis();
            try {
                return doQuery();
            }
            finally {
                complete = System.currentTimeMillis();
            }
        }
        
        /**
         * @return Execute the query and return the value
         */
        protected abstract List<PersonAttributes> doQuery();
    }
}
