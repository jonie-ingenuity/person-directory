package org.jasig.services.persondir.spi.gate;

import java.util.Map;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.spi.SimpleAttributeSource;

/**
 * Gate used to dynamically determine if a {@link SimpleAttributeSource} should be 
 * executed
 * 
 * @author Eric Dalquist
 */
public interface SimpleAttributeSourceGate extends AttributeSourceGate {
    boolean checkFind(AttributeQuery<Map<String, Object>> query);
}
