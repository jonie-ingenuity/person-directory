package org.jasig.services.persondir.spi.gate;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.criteria.Criteria;


/**
 * Used to determine if a particular attribute source
 * should be executed or not based on the {@link AttributeQuery} 
 * 
 * @author Eric Dalquist
 */
public interface AttributeSourceGate {
    boolean checkSearch(AttributeQuery<Criteria> query);
}
