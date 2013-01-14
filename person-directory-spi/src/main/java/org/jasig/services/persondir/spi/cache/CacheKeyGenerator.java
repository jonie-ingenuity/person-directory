package org.jasig.services.persondir.spi.cache;

import java.io.Serializable;
import java.util.Map;

import org.jasig.services.persondir.criteria.Criteria;

/**
 * Generates cache keys for attribute queries.
 * 
 * @author Eric Dalquist
 */
public interface CacheKeyGenerator {
    Serializable generateCacheKey(Criteria c);
    
    Serializable generateCacheKey(Map<String, Object> m);
}
