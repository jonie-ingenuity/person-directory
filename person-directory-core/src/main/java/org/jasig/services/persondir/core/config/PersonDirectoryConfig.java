package org.jasig.services.persondir.core.config;

import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.jasig.services.persondir.spi.cache.CacheKeyGenerator;

import net.sf.ehcache.Ehcache;

public interface PersonDirectoryConfig {
    Set<AttributeSourceConfig<?, ?>> getSourceConfigs();
    
    String getPrimaryIdAttribute();
    
    Ehcache getMergeCache();
    
    ExecutorService getExecutorService();
    
    int getDefaultMaxResults();
    
    int getDefaultQueryTimeout();

    CacheKeyGenerator getCacheKeyGenerator();
}
