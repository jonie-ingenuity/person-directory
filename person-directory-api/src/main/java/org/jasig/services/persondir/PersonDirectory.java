package org.jasig.services.persondir;

import java.util.List;
import java.util.Map;
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
     * Do a simple search for people using an attribute map, this is equivalent to doing
     * a {@link Criteria} search using only OR and EQUALS operators.
     * 
     * @return Immutable list of results
     */
    List<Person> searchForPeople(Map<String, Object> attributes);
    
    /**
     * Do a {@link Criteria} search. This allows for more complex search logic to be used.
     * 
     * @return Immutable list of results
     */
    List<Person> searchForPeople(Criteria query);
    
    /**
     * @return Set of attribute names that can be used when searching for a {@link Person}
     */
    Set<String> getSearchableAttributeNames();
    
    /**
     * @return Set of attribute names that will be included in a returned {@link Person}
     */
    Set<String> getAvailableAttributeNames();
}
