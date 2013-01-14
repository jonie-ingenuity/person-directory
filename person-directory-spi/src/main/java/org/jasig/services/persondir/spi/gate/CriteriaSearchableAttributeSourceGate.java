package org.jasig.services.persondir.spi.gate;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.spi.CriteriaSearchableAttributeSource;

/**
 * Gate used to dynamically determine if a {@link CriteriaSearchableAttributeSource} should be 
 * executed
 * 
 * @author Eric Dalquist
 */
public interface CriteriaSearchableAttributeSourceGate extends AttributeSourceGate {
    boolean checkCriteriaSearch(AttributeQuery<Criteria> query);
}
