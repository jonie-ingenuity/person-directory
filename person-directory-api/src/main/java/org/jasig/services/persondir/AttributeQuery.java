package org.jasig.services.persondir;

/**
 * Base interface for attribute queries
 * 
 * @author Eric Dalquist
 */
public final class AttributeQuery<Q> {
    private final Q query;
    private final int maxResults;
    private final int queryTimeout;
    
    public AttributeQuery(Q query, int maxResults, int queryTimeout) {
        this.query = query;
        this.maxResults = maxResults;
        this.queryTimeout = queryTimeout;
    }

    /**
     * @return The query to execute
     */
    public Q getQuery() {
        return this.query;
    }
    
    /**
     * @return The maximum number of results to return, 0 means no limit.
     */
    public int getMaxResults() {
        return this.maxResults;
    }
    
    /**
     * @return The maximum time in milliseconds to allow for query execution.
     */
    public int getQueryTimeout() {
        return this.queryTimeout;
    }
}
