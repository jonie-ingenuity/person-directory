package org.jasig.services.persondir.util.criteria;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jasig.services.persondir.criteria.BaseCriteriaProcessor;

/**
 * Collects all attribute names from the criteria in an ordered set.
 * 
 * @author Eric Dalquist
 */
public class AttributeNamesCriteriaProcessor extends BaseCriteriaProcessor {
    private final Set<String> attributeNames = new LinkedHashSet<String>();

    public Set<String> getAttributeNames() {
        return attributeNames;
    }

    @Override
    public void appendCompare(String name, Object value) {
        attributeNames.add(name);
    }
}
