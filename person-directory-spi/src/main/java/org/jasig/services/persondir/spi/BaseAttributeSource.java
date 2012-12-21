package org.jasig.services.persondir.spi;

import java.util.Set;

public interface BaseAttributeSource {
    /**
     * @return Set of attributes this source might return, may be an empty set of the source does not know.
     */
    Set<String> getAvailableAttributes();
}
