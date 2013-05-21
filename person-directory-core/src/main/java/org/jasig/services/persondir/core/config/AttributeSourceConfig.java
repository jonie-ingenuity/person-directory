package org.jasig.services.persondir.core.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.Ehcache;

import org.jasig.services.persondir.spi.BaseAttributeSource;
import org.jasig.services.persondir.spi.gate.AttributeSourceGate;

/**
 * Configuration common to all attribute sources
 * 
 * @author Eric Dalquist
 * @param <S> The {@link BaseAttributeSource} type that is being configured
 */
public interface AttributeSourceConfig<S extends BaseAttributeSource> {
    /**
     * @return The name of the attribute source, will never be null.
     */
    String getName();

    /**
     * @return The {@link BaseAttributeSource} instance the configuration is for, will never be null.
     */
    S getAttributeSource();
    
    /**
     * @return Gates to check before executing a query against the attribute source.
     * 
     * @see AttributeSourceBuilder#addGate(AttributeSourceGate...)
     */
    List<AttributeSourceGate> getGates();

    /**
     * @return Optional cache for query results, if null is returned no result caching is done
     * 
     * @see AttributeSourceBuilder#setResultCache(Ehcache)
     * @see AttributeSourceBuilder#setResultCacheName(String)
     */
    Ehcache getResultCache();

    /**
     * @return Optional cache for query misses, if null is returned no miss caching is done
     * 
     * @see AttributeSourceBuilder#setMissCache(Ehcache)
     * @see AttributeSourceBuilder#setMissCacheName(String)
     */
    Ehcache getMissCache();

    /**
     * @return Optional cache for query errors, if null is returned no error caching is done
     * 
     * @see AttributeSourceBuilder#setErrorCache(Ehcache)
     * @see AttributeSourceBuilder#setErrorCacheName(String)
     */
    Ehcache getErrorCache();

    /**
     * @return Maximum time to wait for a query result in milliseconds, a value &lt;= 0 means no timeout
     * 
     * @see AttributeSourceBuilder#setQueryTimeout(long)
     */
    long getQueryTimeout();

    /**
     * @return The action to take upon query timeout
     * 
     * @see AttributeSourceBuilder#setTimeoutBehavior(TimeoutBehavior)
     */
    TimeoutBehavior getTimeoutBehavior();

    /**
     * If true attributes returned by the query that are not explicitly specified as keys in {@link #getAttributeMapping()}
     * will be ignored.
     * 
     * @see AttributeSourceBuilder#setIgnoreUnmappedAttributes(boolean)
     */
    boolean isIgnoreUnmappedAttributes();

    /**
     * Mapping of source/private attribute names (the keys) to the client/public attribute
     * names (the values).
     * 
     * @see AttributeSourceBuilder#addAttributeMapping(String, String)
     */
    Map<String, Collection<String>> getAttributeMapping();
    
    /**
     * Mapping of client/public attribute names (the keys) to the source/private attribute
     * names (the values)
     * 
     * @see #getAttributeMapping()
     */
    Map<String, Collection<String>> getReverseAttributeMapping();

    /**
     * @return Attributes that MUST be included in any attribute query, may be empty, never null.
     * The attribute names are the client/public side attribute names.
     * 
     * @see AttributeSourceBuilder#addRequiredAttribute(String...)
     */
    Set<String> getRequiredQueryAttributes();

    /**
     * @return Attributes that MAY be included in any attribute query, may be empty, never null.
     * The attribute names are the client/public side attribute names.
     * 
     * @see AttributeSourceBuilder#addOptionalAttribute(String...)
     */
    Set<String> getOptionalQueryAttributes();
    
    /**
     * @return Optional set of attributes returned by this source, may be empty, never null.
     * The attribute names are the client/public side attribute names.
     * 
     * @see AttributeSourceBuilder#addAvailableAttribute(String...)
     */
    Set<String> getAvailableAttributes();

}