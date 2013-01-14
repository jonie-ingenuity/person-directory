package org.jasig.services.persondir.spi.gate;

import java.util.Map;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.spi.SimpleSearchableAttributeSource;

/**
 * Gate used to dynamically determine if a {@link SimpleSearchableAttributeSource} should be 
 * executed
 * 
 * @author Eric Dalquist
 */
public interface SimpleSearchableAttributeSourceGate extends AttributeSourceGate {
    boolean checkSimpleSearch(AttributeQuery<Map<String, Object>> query);
}
