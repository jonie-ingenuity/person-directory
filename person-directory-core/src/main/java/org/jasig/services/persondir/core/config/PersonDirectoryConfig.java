package org.jasig.services.persondir.core.config;

import java.util.Set;

import net.sf.ehcache.Ehcache;

public interface PersonDirectoryConfig {
    Set<AttributeSourceConfig<?>> getSourceConfigs();
    
    String getPrimaryIdAttribute();
    
    String getMergeCacheName();
    
    Ehcache getMergeCache();
}
