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
    T addGate(AttributeSourceGate... gate);
    
    T setResultCacheName(String cacheName);

    T setResultCache(Ehcache cache);

    T setMissCacheName(String cacheName);

    T setMissCache(Ehcache cache);

    T setErrorCacheName(String cacheName);

    T setErrorCache(Ehcache cache);

    T setQueryTimeout(long queryTimeout);

    T setTimeoutBehavior(TimeoutBehavior timeoutBehavior);

    T setIgnoreUnmappedAttributes(boolean ignoreUnmappedAttributes);

    T addAttributeMapping(String sourceAttribute, String directoryAttribute);

    T addRequiredAttribute(String... attribute);

    T addOptionalAttribute(String... attribute);

    T addAvailableAttribute(String... attribute);
}