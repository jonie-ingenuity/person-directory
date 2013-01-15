package org.jasig.services.persondir.core.config;



public interface SearchableAttributeSourceBuilder<T extends SearchableAttributeSourceBuilder<T>>
        extends AttributeSourceBuilder<T> {

    T setMaxResults(int maxResults);

}