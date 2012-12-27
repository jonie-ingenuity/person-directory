package org.jasig.services.persondir.spi;

/**
 * Base interface for attribute queries
 * 
 * @author Eric Dalquist
 */
public interface AttributeQuery<Q> {
    
    /**
     * @return The query to execute
     */
    Q getQuery();
    
    /**
     * @return The maximum number of results to return, 0 means no limit.
     */
    int getMaxResults();
    
    /**
     * @return The maximum time in milliseconds to allow for query execution.
     */
    int getQueryTimeout();
}
