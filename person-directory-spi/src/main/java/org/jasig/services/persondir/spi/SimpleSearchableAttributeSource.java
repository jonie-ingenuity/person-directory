package org.jasig.services.persondir.spi;

import java.util.List;
import java.util.Map;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.PersonAttributes;

/**
 * Source of attributes that works on a single person, this source is not searchable for multiple
 * results.
 * 
 * @author Eric Dalquist
 */
public interface SimpleSearchableAttributeSource extends BaseAttributeSource {
    /**
     * Searches for multiple people via the map of attributes, always returns an immutable list
     * 
     * @param query The query to execute
     * @return
     */
    List<PersonAttributes> searchForAttributes(AttributeQuery<Map<String, Object>> query);
}
