package org.jasig.services.persondir.core;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jasig.services.persondir.Person;
import org.jasig.services.persondir.PersonDirectory;
import org.jasig.services.persondir.core.config.PersonDirectoryConfig;
import org.jasig.services.persondir.criteria.Criteria;

import com.google.common.collect.ImmutableMap;

public class PersonDirectoryImpl implements PersonDirectory {
    
    private final PersonDirectoryConfig config;
    //TODO probably break out the individual sources by simple/search/criteria types
    
    public PersonDirectoryImpl(PersonDirectoryConfig config) {
        this.config = config;
    }

    @Override
    public Person findPerson(String primaryId) {
        final Map<String, Object> attributes = ImmutableMap.<String, Object>of(config.getPrimaryIdAttribute(), primaryId);
        
        final List<Person> results = this.searchForPeople(attributes);
        
        if (results.isEmpty()) {
            return null;
        }
        
        if (results.size() > 1) {
            //TODO better exception
            throw new IllegalStateException(results.size() + " results were returned for findPerson(" + primaryId + "), 0 or 1 results was expected");
        }
        
        return results.get(0);
    }

    @Override
    public List<Person> searchForPeople(Map<String, Object> attributes) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Person> searchForPeople(Criteria query) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> getSearchableAttributeNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> getAvailableAttributeNames() {
        // TODO Auto-generated method stub
        return null;
    }

}
