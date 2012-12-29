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
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

abstract class AbstractAttributeSourceConfigBuilder<
            T extends AttributeSourceBuilder<T>, 
            S extends BaseAttributeSource> 
        extends AbstractConfigBuilder<T> 
        implements AttributeSourceBuilder<T>, AttributeSourceConfig<S> {
    
    private final S source;
    private volatile String resultCacheName;
    private volatile Ehcache resultCache;
    private volatile String missCacheName;
    private volatile Ehcache missCache;
    private volatile String errorCacheName;
    private volatile Ehcache errorCache;
    private volatile long queryTimeout = -1;
    private volatile TimeoutBehavior timeoutBehavior;
    private volatile MergeBehavior mergeBehavior;
    private volatile int mergeOrder;
    private volatile boolean ignoreUnmappedAttributes = false;
    private List<AttributeSourceFilter> filters = new ArrayList<AttributeSourceFilter>();
    private Multimap<String, String> attributeMapping = HashMultimap.create();
    private Set<String> requiredAttributes = new HashSet<String>();
    private Set<String> optionalAttributes = new HashSet<String>();
    private Set<String> availableAttributes = new HashSet<String>();
    
    AbstractAttributeSourceConfigBuilder(Class<T> type, S source) {
        super(type);
        Assert.notNull(source, "source cannot be null");
        
        this.source = source;
    }
    
    @Override
    protected void doResolveConfiguration() {
        final BeanFactory beanFactory = this.getBeanFactory();
        
        //Resolve result cache
        if (this.resultCache == null && this.resultCacheName != null) {
            this.resultCache = beanFactory.getBean(this.resultCacheName, Ehcache.class);
        }
        else if (this.resultCacheName == null && this.resultCache != null) {
            this.resultCacheName = this.resultCache.getName();
        }
        
        //Resolve miss cache
        if (this.missCache == null && this.missCacheName != null) {
            this.missCache = beanFactory.getBean(this.missCacheName, Ehcache.class);
        }
        else if (this.missCacheName == null && this.missCache != null) {
            this.missCacheName = this.missCache.getName();
        }
        
        //Resolve error cache
        if (this.errorCache == null && this.errorCacheName != null) {
            this.errorCache = beanFactory.getBean(this.errorCacheName, Ehcache.class);
        }
        else if (this.errorCacheName == null && this.errorCache != null) {
            this.errorCacheName = this.errorCache.getName();
        }
        
        //Make collections immutable as these can't easily be changed at runtime
        this.filters = ImmutableList.copyOf(this.filters);
        this.attributeMapping = ImmutableMultimap.copyOf(this.attributeMapping);
        this.requiredAttributes = ImmutableSet.copyOf(this.requiredAttributes);
        this.optionalAttributes = ImmutableSet.copyOf(this.optionalAttributes);
        this.availableAttributes = ImmutableSet.copyOf(this.availableAttributes);
    }


    @Override
    public final T addFilter(AttributeSourceFilter... filter) {
        for (final AttributeSourceFilter f : filter) {
            filters.add(f);
        }
        return this.getThis();
    }
    
    @Override
    public final T setResultCacheName(String cacheName) {
        final BeanFactory beanFactory = this.getBeanFactory();
        if (this.resultCache != null && beanFactory == null) {
            this.logger.warn("Overwriting resultCache property of '" + this.resultCache.getName() + "' on source 'TODO' with resultCacheName of: '" + cacheName);
            this.resultCache = null;
        }
        this.resultCacheName = cacheName;
        if (beanFactory != null) {
            this.resultCache = beanFactory.getBean(this.resultCacheName, Ehcache.class);
        }
        return this.getThis();
    }
    
    @Override
    public final T setResultCache(Ehcache cache) {
        final BeanFactory beanFactory = this.getBeanFactory();
        if (this.resultCacheName != null && beanFactory == null) {
            this.logger.warn("Overwriting resultCacheName property of '" + this.resultCacheName + "' on source 'TODO' with resultCache of: '" + cache.getName());
            this.resultCacheName = null;
        }
        this.resultCache = cache;
        if (beanFactory != null) {
            this.resultCacheName = this.resultCache.getName();
        }
        return this.getThis();
    }
    
    @Override
    public final T setMissCacheName(String cacheName) {
        final BeanFactory beanFactory = this.getBeanFactory();
        if (this.missCache != null && beanFactory == null) {
            this.logger.warn("Overwriting missCache property of '" + this.missCache.getName() + "' on source 'TODO' with missCacheName of: '" + cacheName);
            this.missCache = null;
        }
        this.missCacheName = cacheName;
        if (beanFactory != null) {
            this.missCache = beanFactory.getBean(this.missCacheName, Ehcache.class);
        }
        return this.getThis();
    }
    
    @Override
    public final T setMissCache(Ehcache cache) {
        final BeanFactory beanFactory = this.getBeanFactory();
        if (this.missCacheName != null && beanFactory == null) {
            this.logger.warn("Overwriting missCacheName property of '" + this.missCacheName + "' on source 'TODO' with missCache of: '" + cache.getName());
            this.missCacheName = null;
        }
        this.missCache = cache;
        if (beanFactory != null) {
            this.missCacheName = this.missCache.getName();
        }
        return this.getThis();
    }
    
    @Override
    public final T setErrorCacheName(String cacheName) {
        final BeanFactory beanFactory = this.getBeanFactory();
        if (this.errorCache != null && beanFactory == null) {
            this.logger.warn("Overwriting errorCache property of '" + this.errorCache.getName() + "' on source 'TODO' with errorCacheName of: '" + cacheName);
            this.errorCache = null;
        }
        this.errorCacheName = cacheName;
        if (beanFactory != null) {
            this.errorCache = beanFactory.getBean(this.errorCacheName, Ehcache.class);
        }
        return this.getThis();
    }
    
    @Override
    public final T setErrorCache(Ehcache cache) {
        final BeanFactory beanFactory = this.getBeanFactory();
        if (this.errorCacheName != null && beanFactory == null) {
            this.logger.warn("Overwriting errorCacheName property of '" + this.errorCacheName + "' on source 'TODO' with errorCache of: '" + cache.getName());
            this.errorCacheName = null;
        }
        this.errorCache = cache;
        if (beanFactory != null) {
            this.errorCacheName = this.errorCache.getName();
        }
        return this.getThis();
    }
    
    @Override
    public final T setQueryTimeout(long queryTimeout) {
        this.queryTimeout = queryTimeout;
        return this.getThis();
    }
    
    @Override
    public final T setTimeoutBehavior(TimeoutBehavior timeoutBehavior) {
        this.timeoutBehavior = timeoutBehavior;
        return this.getThis();
    }

    @Override
    public final T setMergeBehavior(MergeBehavior mergeBehavior) {
        this.mergeBehavior = mergeBehavior;
        return this.getThis();
    }

    @Override
    public final T setMergeOrder(int mergeOrder) {
        this.mergeOrder = mergeOrder;
        return this.getThis();
    }
    
    @Override
    public final T setIgnoreUnmappedAttributes(boolean ignoreUnmappedAttributes) {
        this.ignoreUnmappedAttributes = ignoreUnmappedAttributes;
        return this.getThis();
    }
    
    @Override
    public final T addAttributeMapping(String sourceAttribute, String directoryAttribute) {
        this.attributeMapping.put(sourceAttribute, directoryAttribute);
        return this.getThis();
    }
    
    @Override
    public final T addRequiredAttribute(String... attribute) {
        for (final String attr : attribute) {
            this.requiredAttributes.add(attr);
        }
        return this.getThis();
    }
    
    @Override
    public final T addOptionalAttribute(String... attribute) {
        for (final String attr : attribute) {
            this.optionalAttributes.add(attr);
        }
        return this.getThis();
    }
    
    @Override
    public final T addAvailableAttribute(String... attribute) {
        for (final String attr : attribute) {
            this.availableAttributes.add(attr);
        }
        return this.getThis();
    }
    
    @Override
    public final int getOrder() {
        return this.mergeOrder;
    }

    @Override
    public final S getAttributeSource() {
        return this.source;
    }
    
    @Override
    public final List<AttributeSourceFilter> getFilters() {
        return filters;
    }

    @Override
    public final Ehcache getResultCache() {
        return resultCache;
    }

    @Override
    public final Ehcache getMissCache() {
        return missCache;
    }

    @Override
    public final Ehcache getErrorCache() {
        return errorCache;
    }

    @Override
    public final long getQueryTimeout() {
        return queryTimeout;
    }

    @Override
    public final TimeoutBehavior getTimeoutBehavior() {
        return timeoutBehavior;
    }

    @Override
    public final MergeBehavior getMergeBehavior() {
        return mergeBehavior;
    }

    @Override
    public final int getMergeOrder() {
        return mergeOrder;
    }
    
    @Override
    public final boolean isIgnoreUnmappedAttributes() {
        return ignoreUnmappedAttributes;
    }

    @Override
    public final Map<String, Collection<String>> getAttributeMapping() {
        return attributeMapping.asMap();
    }

    @Override
    public final Set<String> getRequiredQueryAttributes() {
        return requiredAttributes;
    }

    @Override
    public final Set<String> getOptionalQueryAttributes() {
        return optionalAttributes;
    }

    @Override
    public final Set<String> getAvailableAttributes() {
        return availableAttributes;
    }
}
