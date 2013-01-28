package org.jasig.services.persondir.criteria;

import java.util.List;
import java.util.Map;

/**
 * Criteria for a direct equality test.
 * 
 * If the EqualsCriteria value is null an attribute not existing, having a null value,
 * having an empty value list, or containing null in the value list will all result in
 * {@link #matches(Map)} returning true. 
 * 
 * @author Eric Dalquist
 */
public class EqualsCriteria extends CompareCriteria<Object> {

    public EqualsCriteria(String attribute, Object value) {
        super(attribute, value);
    }

    @Override
    public boolean matches(Map<String, List<Object>> attributes) {
        final List<Object> attrValues = attributes.get(this.getAttribute());
        final Object critValue = this.getValue();
        
        //Handle a null criteria value
        if (critValue == null && (attrValues == null || attrValues.isEmpty())) {
            return true;
        }
        
        //Handle a null attribute value
        if (attrValues == null) {
            return false;
        }
        
        //Check each attribute value
        for (final Object attrValue : attrValues) {
            if (attrValue == critValue || (attrValue != null && attrValue.equals(critValue))) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public void process(CriteriaProcessor builder) {
        builder.appendEquals(this);
    }
}
