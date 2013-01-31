package org.jasig.services.persondir.criteria;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Utilities used by the various criteria during matches checks
 */
final class MatchUtils {
    private MatchUtils() {
    }
    
    /**
     * Tries to convert the value to a BigDecimal, if it cannot be converted
     * null is returned.
     */
    public static BigDecimal toBigDecimal(final Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }
        if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
            return BigDecimal.valueOf(((Number)value).longValue());
        }
        if (value instanceof Float || value instanceof Double) {
            return  BigDecimal.valueOf(((Number)value).doubleValue());
        }

        try {
            return new BigDecimal(value.toString());
        }
        catch (final NumberFormatException e) {
            return null;
        }
    }
}
