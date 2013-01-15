package org.jasig.services.persondir.spi.cache;

import java.io.Serializable;
import java.util.Map;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.criteria.Criteria;

/**
 * Generates cache keys for attribute queries.
 * 
 * @author Eric Dalquist
 */
public interface CacheKeyGenerator {
    Serializable generateCriteriaCacheKey(AttributeQuery<Criteria> c);
    
    Serializable generateMapCacheKey(AttributeQuery<Map<String, Object>> m);
}
