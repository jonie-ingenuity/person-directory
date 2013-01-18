package org.jasig.services.persondir.util.criteria;

import java.util.HashSet;
import java.util.Set;

public class AttributeNamesCriteriaProcessor extends BaseCriteriaProcessor {
    private final Set<String> attributeNames = new HashSet<String>();

    public Set<String> getAttributeNames() {
        return attributeNames;
    }

    @Override
    public void appendCompare(String name, Object value) {
        attributeNames.add(name);
    }
}
