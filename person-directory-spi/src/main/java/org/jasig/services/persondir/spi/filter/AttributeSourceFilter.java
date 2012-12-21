package org.jasig.services.persondir.spi.filter;

import java.util.List;
import java.util.Map;

import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.criteria.Criteria;

/**
 * TODO this API seems gross, how do we clean it up? Per-source type filter interfaces with some support classes for common impls?
 * 
 * @author Eric Dalquist
 */
public interface AttributeSourceFilter {
    List<PersonAttributes> doFilterCriteriaSearch(Criteria criteria, FilterChain chain);
    
    List<PersonAttributes> doFilterSimpleSearch(Map<String, Object> searchAttributes, FilterChain chain);
    
    PersonAttributes doFilterFind(Map<String, Object> searchAttributes, FilterChain chain);
}
