package org.jasig.services.persondir.criteria;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class ComparableCriteria extends CompareCriteria<Comparable<?>> {
    private BigDecimal parsedCompareNumber;
    private boolean numberParsed = false;

    public ComparableCriteria(String attribute, Comparable<?> value) {
        super(attribute, value);
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
    }

    @Override
    public final boolean matches(Map<String, List<Object>> attributes) {
        final List<Object> attrValues = attributes.get(this.getAttribute());
        if (attrValues == null || attrValues.isEmpty()) {
            return false;
        }
        
        //Get the value to compare against
        final Comparable<?> compareValue = (Comparable<?>)this.getValue();
        
        for (final Object attrValue : attrValues) {
            //Ignore null attribute values
            if (attrValue == null) {
                continue;
            }
            
            //If the compare value and attribute value are of the same type just do a direct comparison
            if (compareValue.getClass().equals(attrValue.getClass())) {
                @SuppressWarnings("unchecked")
                final Comparable<Object> comparableValue = (Comparable<Object>)compareValue;
                return compare(comparableValue, attrValue);
            }
            
            //Try doing a number comparison
            final BigDecimal compareNumber = getCompareNumber();
            if (compareNumber != null) {
                final BigDecimal attrNumber = MatchUtils.toBigDecimal(attrValue);
                if (attrNumber != null) {
                    return compare((Comparable<BigDecimal>)compareNumber, attrNumber);
                }
            }

            //Handle Date and its various subclasses
            if (compareValue instanceof Date && attrValue instanceof Date) {
                @SuppressWarnings("unchecked")
                final Comparable<Date> comparableValue = (Comparable<Date>)compareValue;
                return compare(comparableValue, (Date)attrValue);
            }
        }
        
        return false;
    }

    private <T> boolean compare(final Comparable<T> compareValue, T attrValue) {
        //Add sign flip so that subclasses see the logically correct compare result, comparison
        //is done backwards here to reduce the number of casts needed
        final int compareResult = compareValue.compareTo(attrValue) * -1;
        return this.checkComparison(compareResult);
    }
    
    private BigDecimal getCompareNumber() {
        if (numberParsed) {
            return parsedCompareNumber;
        }
        
        //Try parsing the value as a number, mark as parsed to handle parse-failure with boolen flag
        final Comparable<?> compareValue = this.getValue();
        parsedCompareNumber = MatchUtils.toBigDecimal(compareValue);
        numberParsed = true;
        
        return parsedCompareNumber;
    }
    
    /**
     * @param compareResult The result of the {@link Comparable#compareTo(Object)} call
     * @return If the comparison was successful
     */
    protected abstract boolean checkComparison(int compareResult);
}
