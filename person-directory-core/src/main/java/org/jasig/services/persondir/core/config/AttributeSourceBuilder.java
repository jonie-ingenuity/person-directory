package org.jasig.services.persondir.core.config;

import net.sf.ehcache.Ehcache;

import org.jasig.services.persondir.spi.gate.AttributeSourceGate;

/**
 * Base class for configuration of an attribute source.
 * 
 * @author Eric Dalquist
 * @param <T> The concrete type of the superclass, used to allow builder pattern with an abstract class
 * @param <S> The attribute source type
 */
public interface AttributeSourceBuilder<T extends AttributeSourceBuilder<T>> {
    /**
     * Adds {@link AttributeSourceGate}s that are used to check if a query should
     * be executed against this source.
     * 
     * @see AttributeSourceConfig#getGates()
     */
    T addGate(AttributeSourceGate... gate);
    
    /**
     * Set the Spring Bean name of the result cache to use. If set this overrides any
     * previous call to {@link #setResultCache(Ehcache)}
     * 
     * @see AttributeSourceConfig#getResultCache()
     */
    T setResultCacheName(String cacheName);

    /**
     * Set the cache to use for results. If set this overrides any previous call to
     * {@link #setResultCacheName(String)}
     * 
     * @see AttributeSourceConfig#getResultCache()
     */
    T setResultCache(Ehcache cache);
    
    /**
     * Set the Spring Bean name of the miss cache to use. If set this overrides any
     * previous call to {@link #setMissCache(Ehcache)}
     * 
     * @see AttributeSourceConfig#getMissCache()
     */
    T setMissCacheName(String cacheName);

    /**
     * Set the cache to use for misss. If set this overrides any previous call to
     * {@link #setMissCacheName(String)}
     * 
     * @see AttributeSourceConfig#getMissCache()
     */
    T setMissCache(Ehcache cache);
    
    /**
     * Set the Spring Bean name of the error cache to use. If set this overrides any
     * previous call to {@link #setErrorCache(Ehcache)}
     * 
     * @see AttributeSourceConfig#getErrorCache()
     */
    T setErrorCacheName(String cacheName);

    /**
     * Set the cache to use for errors. If set this overrides any previous call to
     * {@link #setErrorCacheName(String)}
     * 
     * @see AttributeSourceConfig#getErrorCache()
     */
    T setErrorCache(Ehcache cache);

    /**
     * Set the maximum time to wait for a query result in milliseconds. A value
     * &lt;= 0 means no timeout.
     * 
     * @see AttributeSourceConfig#getQueryTimeout()
     */
    T setQueryTimeout(long queryTimeout);

    /**
     * Set the action to take when a query times out.
     * 
     * @see AttributeSourceConfig#getQueryTimeout()
     */
    T setTimeoutBehavior(TimeoutBehavior timeoutBehavior);

    /**
     * If set to true any attributes returned by the query that are not explicitly defined as keys
     * in the {@link #addAttributeMapping(String, String)} Map are ignored, defaults to false.
     * 
     * @see AttributeSourceConfig#isIgnoreUnmappedAttributes();
     */
    T setIgnoreUnmappedAttributes(boolean ignoreUnmappedAttributes);

    /**
     * Add a mapping between the sourceAttribute, from the query, to the directoryAttribute
     * which is used by the person directory client.
     * 
     * @see AttributeSourceConfig#getAttributeMapping()
     */
    T addAttributeMapping(String sourceAttribute, String directoryAttribute);

    /**
     * Add an attribute that is required for the query to run. The attribute is a source-side
     * attribute. 
     * 
     * @see AttributeSourceConfig#getRequiredQueryAttributes()
     */
    T addRequiredAttribute(String... sourceAttribute);

    /**
     * Add an attribute that can be used to run a query. The attribute is a source-side
     * attribute. 
     * 
     * @see AttributeSourceConfig#getOptionalQueryAttributes()
     */
    T addOptionalAttribute(String... sourceAttribute);

    /**
     * Add an attribute that may be returned by this query. The attribute is a source-side
     * attribute. 
     * 
     * @see AttributeSourceConfig#getAvailableAttributes()
     */
    T addAvailableAttribute(String... sourceAttribute);
}