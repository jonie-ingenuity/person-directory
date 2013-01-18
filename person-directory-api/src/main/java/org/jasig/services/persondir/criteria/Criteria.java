package org.jasig.services.persondir.criteria;

import java.util.List;
import java.util.Map;

public interface Criteria {
    boolean equals(Map<String, List<Object>> attributes);
    
    Criteria getNegatedForm();
}
