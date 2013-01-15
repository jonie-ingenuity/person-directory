package org.jasig.services.persondir.core.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.Ehcache;

import org.jasig.services.persondir.spi.BaseAttributeSource;
import org.jasig.services.persondir.spi.gate.AttributeSourceGate;
import org.springframework.core.Ordered;

public interface AttributeSourceConfig<S extends BaseAttributeSource> extends Ordered {

    S getAttributeSource();
    
    List<AttributeSourceGate> getGates();

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
    
    Set<String> getAvailableAttributes();

}