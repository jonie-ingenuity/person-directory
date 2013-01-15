package org.jasig.services.persondir.util.criteria;

import java.util.HashSet;
import java.util.Set;

import org.jasig.services.persondir.criteria.CompareCriteria;

public class CriteriaAttributeNamesHandler extends BaseCriteriaHandler {
    private final Set<String> attributeNames = new HashSet<String>();

    public Set<String> getAttributeNames() {
        return attributeNames;
    }

    @Override
    public void handleCompareCriteria(CompareCriteria c, CriteriaWalker walker) {
        attributeNames.add(c.getAttribute());
    }
}
