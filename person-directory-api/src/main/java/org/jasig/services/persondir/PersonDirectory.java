package org.jasig.services.persondir;

import java.util.List;
import java.util.Set;

import org.jasig.services.persondir.criteria.Criteria;

/**
 * Public interface for clients of person directory. Encapsulates search and attribute listing functionality.
 * 
 * @author Eric Dalquist
 */
public interface PersonDirectory {
    /**
     * Find a single {@link Person} using the primary identifier. NULL is returned if no
     * match is found.
     */
    Person findPerson(String primaryId);
    
    /**
     * Do a {@link Criteria} search. This allows for more complex search logic to be used.
     * 
     * @return Immutable list of results
     */
    List<Person> searchForPeople(Criteria criteria);
    List<Person> searchForPeople(AttributeQuery<Criteria> attributeQuery);
    
    /**
     * @return Set of attribute names that can be used when searching for a {@link Person}
     */
    Set<String> getSearchableAttributeNames();
    
    /**
     * @return Set of attribute names that may be included in a returned {@link Person}
     */
    Set<String> getAvailableAttributeNames();
}
