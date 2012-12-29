package org.jasig.services.persondir.core.config;

import java.util.concurrent.ExecutorService;

import net.sf.ehcache.Ehcache;

import org.jasig.services.persondir.PersonDirectory;
import org.jasig.services.persondir.spi.CriteriaSearchableAttributeSource;
import org.jasig.services.persondir.spi.SimpleAttributeSource;
import org.jasig.services.persondir.spi.SimpleSearchableAttributeSource;
import org.springframework.beans.factory.BeanFactory;

public interface PersonDirectoryBuilder {
    PersonDirectoryBuilder setMergeCacheName(String mergeCacheName);
    
    PersonDirectoryBuilder setMergeCache(Ehcache mergeCacheName);
    
    PersonDirectoryBuilder setExecutorServiceName(String executorServiceName);
    
    PersonDirectoryBuilder setExecutorService(ExecutorService executorService);
    
    PersonDirectoryBuilder setDefaultMaxResults(int defaultMaxResults);

    PersonDirectoryBuilder setDefaultQueryTimeout(int defaultQueryTimeout);
    
    SimpleAttributeSourceBuilder addAttributeSource(SimpleAttributeSource source);
    
    SimpleSearchableAttributeSourceBuilder addAttributeSource(SimpleSearchableAttributeSource source);
    
    CriteriaSearchableAttributeSourceBuilder addAttributeSource(CriteriaSearchableAttributeSource source);
    
    PersonDirectory build(BeanFactory beanFactory);
}
