package org.jasig.services.persondir.core.config;

import net.sf.ehcache.Ehcache;

public interface PersonDirectoryBuilder {
    PersonDirectoryBuilder setMergeCacheName(String mergeCacheName);
    
    PersonDirectoryBuilder setMergeCache(Ehcache mergeCacheName);
}
