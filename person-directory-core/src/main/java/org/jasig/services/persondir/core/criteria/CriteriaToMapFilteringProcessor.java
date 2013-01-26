package org.jasig.services.persondir.core.criteria;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jasig.services.persondir.criteria.BaseCriteriaProcessor;
import org.jasig.services.persondir.criteria.Criteria;

import com.google.common.base.Function;

/**
 * Converts a {@link Criteria} tree into a simple Map of attributes. Each compare
 * criteria has its attribute name/value placed into the map as the key/value. If
 * one attribute name appears more than once with different values the map value
 * is turned into a Set of the unique values.
 * 
 * @author Eric Dalquist
 */
public final class CriteriaToMapFilteringProcessor extends BaseCriteriaProcessor {
    private final Map<String, Object> attributes = new LinkedHashMap<String, Object>();
    private final Function<String, Boolean> attributeFilter;
    
    public CriteriaToMapFilteringProcessor(Function<String, Boolean> attributeFilter) {
        this.attributeFilter = attributeFilter;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void appendCompare(String name, Object value) {
        if (value != null && this.attributeFilter.apply(name)) {
            final Object existingValue = attributes.get(name);
            if (existingValue == null || existingValue.equals(value)) {
                attributes.put(name, value);
            }
            else {
                if (existingValue instanceof Set) {
                    ((Set<Object>) existingValue).add(value);
                }
                else {
                    final Set<Object> values = new LinkedHashSet<Object>();
                    values.add(existingValue);
                    values.add(value);
                    attributes.put(name, values);
                }
            }
        }
    }
}