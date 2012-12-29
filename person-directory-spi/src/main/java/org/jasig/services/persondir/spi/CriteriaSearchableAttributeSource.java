package org.jasig.services.persondir.spi;

import java.util.List;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.criteria.Criteria;

/**
 * Attribute source that can be searched for multiple results using a {@link Criteria}
 * 
 * @author Eric Dalquist
 */
public interface CriteriaSearchableAttributeSource extends BaseAttributeSource {
    /**
     * Searches for multiple people via the criteria, always returns an immutable list
     * 
     * @param query The query to execute
     * @return
     */
    List<PersonAttributes> searchForAttributes(AttributeQuery<Criteria> query);
}
