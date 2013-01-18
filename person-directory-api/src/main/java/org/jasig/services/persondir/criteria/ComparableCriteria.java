package org.jasig.services.persondir.criteria;

import java.util.List;
import java.util.Map;

public abstract class ComparableCriteria extends CompareCriteria<Comparable<?>> {

    public ComparableCriteria(String attribute, Comparable<?> value) {
        super(attribute, value);
    }

    @Override
    public final boolean equals(Map<String, List<Object>> attributes) {
        @SuppressWarnings("unchecked")
        final Comparable<Object> compareValue = (Comparable<Object>)this.getValue();
        
        final List<Object> values = attributes.get(this.getAttribute());
        for (final Object value : values) {
            if (value instanceof Comparable) {
                @SuppressWarnings("unchecked")
                final Comparable<Object> comparableValue = (Comparable<Object>)value;
                if (compare(compareValue, comparableValue)) {
                    return true;
                }
            }
            else {
                throw new IllegalArgumentException("Attribute '" + this.getAttribute() + "' has a value of type " + value.getClass().getName() + " which does not implement Comparable");
            }
        }
        
        return false;
    }
    
    protected abstract boolean compare(Comparable<Object> compareValue, Comparable<Object> attributeValue);
}
