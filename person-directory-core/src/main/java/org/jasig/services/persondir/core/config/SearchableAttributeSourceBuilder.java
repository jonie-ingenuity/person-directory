package org.jasig.services.persondir.core.config;



/**
 * @author Eric Dalquist
 * @param <T> The concrete type of the superclass, used to allow builder pattern with an abstract class
 */
public interface SearchableAttributeSourceBuilder<T extends SearchableAttributeSourceBuilder<T>>
        extends AttributeSourceBuilder<T> {

    /**
     * @param maxResults The maximum number of results to be returned.
     * 
     * @see SearchableAttributeSourceConfig#getMaxResults()
     */
    T setMaxResults(int maxResults);

}