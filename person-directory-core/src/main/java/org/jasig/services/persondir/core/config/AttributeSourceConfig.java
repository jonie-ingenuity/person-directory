package org.jasig.services.persondir.core.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.Ehcache;

import org.jasig.services.persondir.spi.BaseAttributeSource;
import org.jasig.services.persondir.spi.filter.AttributeSourceFilter;

public interface AttributeSourceConfig<S extends BaseAttributeSource> {

    S getAttributeSource();

    List<AttributeSourceFilter> getFilters();

    String getResultCacheName();

    Ehcache getResultCache();

    String getMissCacheName();

    Ehcache getMissCache();

    String getErrorCacheName();

    Ehcache getErrorCache();

    long getQueryTimeout();

    TimeoutBehavior getTimeoutBehavior();

    MergeBehavior getMergeBehavior();

    int getMergeOrder();

    boolean isIgnoreUnmappedAttributes();

    Map<String, Collection<String>> getAttributeMapping();

    Set<String> getRequiredAttributes();

    Set<String> getOptionalAttributes();

    Set<String> getAvailableAttributes();

}