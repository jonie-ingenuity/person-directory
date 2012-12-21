package org.jasig.services.persondir.core.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.Ehcache;

import org.jasig.services.persondir.spi.BaseAttributeSource;
import org.jasig.services.persondir.spi.filter.AttributeSourceFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

/**
 * Base class for configuration of an attribute source.
 * 
 * @author Eric Dalquist
 * @param <T> The concrete type of the superclass, used to allow builder pattern with an abstract class
 * @param <S> The attribute source type
 */
abstract class AbstractAttributeSourceBuilder<T extends AbstractAttributeSourceBuilder<T, S>, S extends BaseAttributeSource> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final Class<T> type;
    private final S source;
    private final List<AttributeSourceFilter> filters = new ArrayList<AttributeSourceFilter>();
    private String resultCacheName;
    private Ehcache resultCache;
    private String missCacheName;
    private Ehcache missCache;
    private String errorCacheName;
    private Ehcache errorCache;
    private long queryTimeout = -1;
    private TimeoutBehavior timeoutBehavior;
    private MergeBehavior mergeBehavior;
    private int mergeOrder;
    private boolean ignoreUnmappedAttributes = false;
    private final SetMultimap<String, String> attributeMapping = HashMultimap.create();
    private final Set<String> requiredAttributes = new HashSet<String>();
    private final Set<String> optionalAttributes = new HashSet<String>();
    private final Set<String> availableAttributes = new HashSet<String>();
    
    AbstractAttributeSourceBuilder(Class<T> type, S source) {
        Assert.notNull(type, "type cannot be null");
        Assert.notNull(source, "source cannot be null");
        
        if (!this.getClass().equals(type)) {
            throw new Error(this.getClass().getName() + " has an illegal generics configuration. The generic type and the concrete type must be the same");
        }
        
        this.type = type;
        this.source = source;
    }
    
    /**
     * Adds a filter to be wrapped around the attribute source
     */
    public final T addFilter(AttributeSourceFilter... filter) {
        for (final AttributeSourceFilter f : filter) {
            filters.add(f);
        }
        return this.getThis();
    }
    
    public final T setResultCacheName(String cacheName) {
        if (this.resultCache != null) {
            this.logger.warn("Overwriting resultCache property of '" + this.resultCache.getName() + "' on source 'TODO' with resultCacheName of: '" + cacheName);
            this.resultCache = null;
        }
        this.resultCacheName = cacheName;
        return this.getThis();
    }
    
    public final T setResultCache(Ehcache cache) {
        if (this.resultCacheName != null) {
            this.logger.warn("Overwriting resultCacheName property of '" + this.resultCacheName + "' on source 'TODO' with resultCache of: '" + cache.getName());
            this.resultCacheName = null;
        }
        this.resultCache = cache;
        return this.getThis();
    }
    
    public final T setMissCacheName(String cacheName) {
        if (this.missCache != null) {
            this.logger.warn("Overwriting missCache property of '" + this.missCache.getName() + "' on source 'TODO' with missCacheName of: '" + cacheName);
            this.missCache = null;
        }
        this.missCacheName = cacheName;
        return this.getThis();
    }
    
    public final T setMissCache(Ehcache cache) {
        if (this.missCacheName != null) {
            this.logger.warn("Overwriting missCacheName property of '" + this.missCacheName + "' on source 'TODO' with missCache of: '" + cache.getName());
            this.missCacheName = null;
        }
        this.missCache = cache;
        return this.getThis();
    }
    
    public final T setErrorCacheName(String cacheName) {
        if (this.errorCache != null) {
            this.logger.warn("Overwriting errorCache property of '" + this.errorCache.getName() + "' on source 'TODO' with errorCacheName of: '" + cacheName);
            this.errorCache = null;
        }
        this.errorCacheName = cacheName;
        return this.getThis();
    }
    
    public final T setErrorCache(Ehcache cache) {
        if (this.errorCacheName != null) {
            this.logger.warn("Overwriting errorCacheName property of '" + this.errorCacheName + "' on source 'TODO' with errorCache of: '" + cache.getName());
            this.errorCacheName = null;
        }
        this.errorCache = cache;
        return this.getThis();
    }
    
    public final T setQueryTimeout(long queryTimeout) {
        this.queryTimeout = queryTimeout;
        return this.getThis();
    }
    
    public final T setTimeoutBehavior(TimeoutBehavior timeoutBehavior) {
        this.timeoutBehavior = timeoutBehavior;
        return this.getThis();
    }

    public final T setMergeBehavior(MergeBehavior mergeBehavior) {
        this.mergeBehavior = mergeBehavior;
        return this.getThis();
    }

    public final T setMergeOrder(int mergeOrder) {
        this.mergeOrder = mergeOrder;
        return this.getThis();
    }
    
    public final T setIgnoreUnmappedAttributes(boolean ignoreUnmappedAttributes) {
        this.ignoreUnmappedAttributes = ignoreUnmappedAttributes;
        return this.getThis();
    }
    
    public final T addAttributeMapping(String sourceAttribute, String directoryAttribute) {
        this.attributeMapping.put(sourceAttribute, directoryAttribute);
        return this.getThis();
    }
    
    public final T addRequiredAttribute(String... attribute) {
        for (final String attr : attribute) {
            this.requiredAttributes.add(attr);
        }
        return this.getThis();
    }
    
    public final T addOptionalAttribute(String... attribute) {
        for (final String attr : attribute) {
            this.optionalAttributes.add(attr);
        }
        return this.getThis();
    }
    
    public final T addAvailableAttribute(String... attribute) {
        for (final String attr : attribute) {
            this.availableAttributes.add(attr);
        }
        return this.getThis();
    }

    /**
     * Exists to keep complex cast logic in one place.
     * 
     * @return typed reference to this class
     */
    protected final T getThis() {
        return this.type.cast(this);
    }

    protected final S getAttributeSource() {
        return this.source;
    }
    
    protected List<AttributeSourceFilter> getFilters() {
        return filters;
    }

    protected String getResultCacheName() {
        return resultCacheName;
    }

    protected Ehcache getResultCache() {
        return resultCache;
    }

    protected String getMissCacheName() {
        return missCacheName;
    }

    protected Ehcache getMissCache() {
        return missCache;
    }

    protected String getErrorCacheName() {
        return errorCacheName;
    }

    protected Ehcache getErrorCache() {
        return errorCache;
    }

    protected long getQueryTimeout() {
        return queryTimeout;
    }

    protected TimeoutBehavior getTimeoutBehavior() {
        return timeoutBehavior;
    }

    protected MergeBehavior getMergeBehavior() {
        return mergeBehavior;
    }

    protected int getMergeOrder() {
        return mergeOrder;
    }
    
    protected boolean isIgnoreUnmappedAttributes() {
        return ignoreUnmappedAttributes;
    }

    protected Map<String, Collection<String>> getAttributeMapping() {
        return attributeMapping.asMap();
    }

    protected Set<String> getRequiredAttributes() {
        return requiredAttributes;
    }

    protected Set<String> getOptionalAttributes() {
        return optionalAttributes;
    }

    protected Set<String> getAvailableAttributes() {
        return availableAttributes;
    }
}
