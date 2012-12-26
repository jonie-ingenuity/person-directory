package org.jasig.services.persondir.core;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.Ehcache;

import org.jasig.services.persondir.Person;
import org.jasig.services.persondir.PersonDirectory;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.spi.BaseAttributeSource;

import com.google.common.collect.ImmutableMap;

public class PersonDirectoryImpl implements PersonDirectory {
    
    //TODO this needs to be the configured source ...
    private final Set<BaseAttributeSource> sourceBuilders = null;
    private final String primaryIdAttribute = null;
    private final Ehcache mergeCache = null;

    @Override
    public Person findPerson(String primaryId) {
        final Map<String, Object> attributes = ImmutableMap.<String, Object>of(this.primaryIdAttribute, primaryId);
        
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
