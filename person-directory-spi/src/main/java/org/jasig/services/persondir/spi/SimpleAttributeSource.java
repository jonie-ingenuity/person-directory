package org.jasig.services.persondir.spi;

import java.util.Map;

import org.jasig.services.persondir.PersonAttributes;

/**
 * Source of attributes that works on a single person, this source is not searchable for multiple
 * results.
 * 
 * @author Eric Dalquist
 */
public interface SimpleAttributeSource extends BaseAttributeSource {
    /**
     * Gets the attributes for a specific person
     * 
     * @param criteria The criteria to use to find the attributes
     * @return The attributes for a specific person, returns null if no matching person was found
     */
    PersonAttributes findPersonAttributes(Map<String, Object> searchAttributes);
}
