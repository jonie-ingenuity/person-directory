package org.jasig.services.persondir.criteria;

import java.math.BigDecimal;
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
    private BigDecimal parsedCompareNumber;
    private boolean numberParsed = false;

    public EqualsCriteria(String attribute, Object value) {
        super(attribute, value);
    }

    @Override
    public boolean matches(Map<String, List<Object>> attributes) {
        final List<Object> attrValues = attributes.get(this.getAttribute());
        final Object compareValue = this.getValue();
        
        //Handle a null criteria value
        if (compareValue == null && (attrValues == null || attrValues.isEmpty())) {
            return true;
        }
        
        //Handle a null attribute value
        if (attrValues == null) {
            return false;
        }
        
        //If a the compare value has been parsed as a number just skip the first direct equality check
        if (this.parsedCompareNumber == null) {
            //Check each attribute value for direct equality
            for (final Object attrValue : attrValues) {
                if (attrValue == compareValue || (attrValue != null && attrValue.equals(compareValue))) {
                    return true;
                }
            }
        }
        
        //If the compare value can be parsed as a number try doing number based equality checks
        final BigDecimal compareNumber = this.getCompareNumber();
        if (compareNumber != null) {
            //Check each value for number equality
            for (final Object attrValue : attrValues) {
                final BigDecimal attrNumber = MatchUtils.toBigDecimal(attrValue);
                
                // compareTo used because equals only returns true for exact equality
                // For example 1.compareTo(1.0) == 0 but 1.equals(1.0) == false 
                if (attrNumber != null && attrNumber.compareTo(compareNumber) == 0) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private BigDecimal getCompareNumber() {
        if (numberParsed) {
            return parsedCompareNumber;
        }
        
        //Try parsing the value as a number, mark as parsed to handle parse-failure with boolen flag
        final Object compareValue = this.getValue();
        parsedCompareNumber = MatchUtils.toBigDecimal(compareValue);
        numberParsed = true;
        
        return parsedCompareNumber;
    }
    
    @Override
    public void process(CriteriaProcessor builder) {
        builder.appendEquals(this);
    }
}
