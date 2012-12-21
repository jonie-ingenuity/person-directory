package org.jasig.services.persondir.spi.filter;

import java.util.List;
import java.util.Map;

import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.criteria.Criteria;

public interface FilterChain {
    List<PersonAttributes> doFilterCriteriaSearch(Criteria criteria);
    
    List<PersonAttributes> doFilterSimpleSearch(Map<String, Object> searchAttributes);
    
    PersonAttributes doFilterFind(Map<String, Object> searchAttributes);
}
