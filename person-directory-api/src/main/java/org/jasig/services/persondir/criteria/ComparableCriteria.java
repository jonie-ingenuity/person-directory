package org.jasig.services.persondir.criteria;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class ComparableCriteria extends CompareCriteria<Comparable<?>> {
    private BigDecimal compareNumber;
    private boolean parsed = false;

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
                return compare((Comparable<Object>)compareValue, attrValue);
            }
            
            //Try doing a number comparison
            final BigDecimal compareNumber = getCompareNumber();
            if (compareNumber != null) {
                final BigDecimal attrNumber = toBigDecimal(attrValue);
                if (attrNumber != null) {
                    return compare((Comparable<BigDecimal>)compareNumber, attrNumber);
                }
            }

            //Handle Date and its various subclasses
            if (compareValue instanceof Date && attrValue instanceof Date) {
                return compare((Comparable<Date>)compareValue, (Date)attrValue);
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
        if (parsed) {
            return compareNumber;
        }
        
        //Try parsing the value as a number, mark as parsed to handle parse-failure with boolen flag
        final Comparable<?> compareValue = this.getValue();
        compareNumber = toBigDecimal(compareValue);
        parsed = true;
        
        return compareNumber;
    }
    
    private static BigDecimal toBigDecimal(final Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }
        if (value instanceof Byte || value instanceof Short || 
                value instanceof Integer || value instanceof Long) {
            return new BigDecimal(((Number)value).longValue());
        }
        if (value instanceof Float || value instanceof Double) {
            return new BigDecimal(((Number)value).doubleValue());
        }

        try {
            return new BigDecimal(value.toString());
        }
        catch (final NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * @param compareResult The result of the {@link Comparable#compareTo(Object)} call
     * @return If the comparison was successful
     */
    protected abstract boolean checkComparison(int compareResult);
}
