package org.jasig.services.persondir.core.config;

import net.sf.ehcache.Ehcache;

import org.jasig.services.persondir.PersonDirectory;
import org.jasig.services.persondir.spi.CriteriaSearchableAttributeSource;
import org.jasig.services.persondir.spi.SimpleAttributeSource;
import org.jasig.services.persondir.spi.SimpleSearchableAttributeSource;
import org.springframework.beans.factory.BeanFactory;

public interface PersonDirectoryBuilder {
    PersonDirectoryBuilder setMergeCacheName(String mergeCacheName);
    
    PersonDirectoryBuilder setMergeCache(Ehcache mergeCacheName);
    
    SimpleAttributeSourceBuilder addAttributeSource(SimpleAttributeSource source);
    
    SimpleSearchableAttributeSourceBuilder addAttributeSource(SimpleSearchableAttributeSource source);
    
    CriteriaSearchableAttributeSourceBuilder addAttributeSource(CriteriaSearchableAttributeSource source);
    
    PersonDirectory build(BeanFactory beanFactory);
}
