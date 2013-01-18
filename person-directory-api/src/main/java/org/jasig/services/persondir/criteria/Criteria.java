package org.jasig.services.persondir.criteria;

import java.util.List;
import java.util.Map;

/**
 * Base for logical search criteria
 * 
 * @author Eric Dalquist
 */
public interface Criteria {
    /**
     * Check if this Criteria tree matches the attributes
     */
    boolean matches(Map<String, List<Object>> attributes);
    
    /**
     * @return The negated form of this Criteria structure
     */
    Criteria getNegatedForm();
    
    /**
     * Walks the {@link Criteria} tree calling the methods on {@link CriteriaProcessor} as it goes
     */
    void process(CriteriaProcessor builder);
}
