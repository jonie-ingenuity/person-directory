package org.jasig.services.persondir.spi;

import java.util.Set;

/**
 * Base interface for all attribute sources
 * 
 * @author Eric Dalquist
 */
public interface BaseAttributeSource {
    /**
     * @return Set of attributes names this source might return, may be an empty set of the source does not know.
     * These are the private/source side attribute names.
     */
    Set<String> getAvailableAttributes();
}
