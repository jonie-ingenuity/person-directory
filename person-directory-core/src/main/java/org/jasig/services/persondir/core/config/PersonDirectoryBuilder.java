package org.jasig.services.persondir.core.config;

import java.util.concurrent.ExecutorService;

import net.sf.ehcache.Ehcache;

import org.jasig.services.persondir.PersonDirectory;
import org.jasig.services.persondir.spi.CriteriaSearchableAttributeSource;
import org.jasig.services.persondir.spi.SimpleAttributeSource;
import org.jasig.services.persondir.spi.cache.CacheKeyGenerator;
import org.springframework.beans.factory.BeanFactory;

public interface PersonDirectoryBuilder {
    PersonDirectoryBuilder setMergeCacheName(String mergeCacheName);
    
    PersonDirectoryBuilder setMergeCache(Ehcache mergeCacheName);
    
    PersonDirectoryBuilder setExecutorServiceName(String executorServiceName);
    
    PersonDirectoryBuilder setExecutorService(ExecutorService executorService);
    
    PersonDirectoryBuilder setDefaultMaxResults(int defaultMaxResults);

    PersonDirectoryBuilder setDefaultQueryTimeout(int defaultQueryTimeout);
    
    SimpleAttributeSourceBuilder addAttributeSource(SimpleAttributeSource source);
    
    CriteriaSearchableAttributeSourceBuilder addAttributeSource(CriteriaSearchableAttributeSource source);
    
    PersonDirectory build(BeanFactory beanFactory);

    PersonDirectoryBuilder setCacheKeyGenerator(CacheKeyGenerator cacheKeyGenerator);

    PersonDirectoryBuilder setCacheKeyGeneratorName(String cacheKeyGeneratorName);
}
