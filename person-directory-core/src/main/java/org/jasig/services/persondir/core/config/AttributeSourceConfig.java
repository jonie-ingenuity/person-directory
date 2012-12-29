package org.jasig.services.persondir.core.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.Ehcache;

import org.jasig.services.persondir.spi.BaseAttributeSource;
import org.jasig.services.persondir.spi.filter.AttributeSourceFilter;
import org.springframework.core.Ordered;

public interface AttributeSourceConfig<S extends BaseAttributeSource> extends Ordered {

    S getAttributeSource();

    List<AttributeSourceFilter> getFilters();

    Ehcache getResultCache();

    Ehcache getMissCache();

    Ehcache getErrorCache();

    long getQueryTimeout();

    TimeoutBehavior getTimeoutBehavior();

    MergeBehavior getMergeBehavior();

    int getMergeOrder();

    boolean isIgnoreUnmappedAttributes();

    Map<String, Collection<String>> getAttributeMapping();

    Set<String> getRequiredQueryAttributes();

    Set<String> getOptionalQueryAttributes();
    
    /**
     * @return Superset of {@link #getRequiredQueryAttributes()} and {@link #getOptionalQueryAttributes()}
     */
    Set<String> getQueryAttributes();

    Set<String> getAvailableAttributes();

}