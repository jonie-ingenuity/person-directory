package org.jasig.services.persondir.core.config;

import java.util.LinkedHashSet;
import java.util.Set;

import net.sf.ehcache.Ehcache;

import org.jasig.services.persondir.PersonDirectory;
import org.jasig.services.persondir.spi.CriteriaSearchableAttributeSource;
import org.jasig.services.persondir.spi.SimpleAttributeSource;
import org.jasig.services.persondir.spi.SimpleSearchableAttributeSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Class for working out the details of configuring PD, eventually this will be called by the spring
 * namespace handler based on the XML configuration
 * 
 * @author Eric Dalquist
 */
public final class PersonDirectoryJavaConfigBuilder {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final Set<AbstractAttributeSourceBuilder<?, ?>> sourceBuilders = new LinkedHashSet<AbstractAttributeSourceBuilder<?, ?>>();
    
    private final String primaryIdAttribute;
    private String mergeCacheName;
    private Ehcache mergeCache;

    public PersonDirectoryJavaConfigBuilder(String primaryIdAttribute) {
        Assert.notNull(primaryIdAttribute, "primaryIdAttribute can not be null");
        
        this.primaryIdAttribute = primaryIdAttribute;
    }
    
    public final PersonDirectoryJavaConfigBuilder setMergeCacheName(String cacheName) {
        if (this.mergeCache != null) {
            this.logger.warn("Overwriting mergeCache property of '" + this.mergeCache.getName() + "' on with mergeCacheName of: '" + cacheName);
            this.mergeCache = null;
        }
        this.mergeCacheName = cacheName;
        return this;
    }
    
    public final PersonDirectoryJavaConfigBuilder setMergeCache(Ehcache cache) {
        if (this.mergeCacheName != null) {
            this.logger.warn("Overwriting mergeCacheName property of '" + this.mergeCacheName + "' on with mergeCache of: '" + cache.getName());
            this.mergeCacheName = null;
        }
        this.mergeCache = cache;
        return this;
    }
    
    public SimpleAttributeSourceBuilder addAttributeSource(SimpleAttributeSource source) {
        final SimpleAttributeSourceBuilder sourceBuilder = new SimpleAttributeSourceBuilder(source);
        sourceBuilders.add(sourceBuilder);
        return sourceBuilder;
    }
    
    public SimpleSearchableAttributeSourceBuilder addAttributeSource(SimpleSearchableAttributeSource source) {
        final SimpleSearchableAttributeSourceBuilder sourceBuilder = new SimpleSearchableAttributeSourceBuilder(source);
        sourceBuilders.add(sourceBuilder);
        return sourceBuilder;
    }
    
    public CriteriaSearchableAttributeSourceBuilder addAttributeSource(CriteriaSearchableAttributeSource source) {
        final CriteriaSearchableAttributeSourceBuilder sourceBuilder = new CriteriaSearchableAttributeSourceBuilder(source);
        sourceBuilders.add(sourceBuilder);
        return sourceBuilder;
    }
    
    public PersonDirectory build() {
        //TODO
        return null;
    }
}
