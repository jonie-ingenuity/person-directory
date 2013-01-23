package org.jasig.services.persondir.core.worker;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.jasig.services.persondir.criteria.BaseCriteriaProcessor;

import com.google.common.base.Function;

final class CriteriaToMapProcessor extends BaseCriteriaProcessor {
    private final Map<String, Object> attributes = new LinkedHashMap<String, Object>();
    private final Function<String, Boolean> attributeFilter;
    
    public CriteriaToMapProcessor(Function<String, Boolean> attributeFilter) {
        this.attributeFilter = attributeFilter;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void appendCompare(String name, Object value) {
        if (this.attributeFilter.apply(name)) {
            final Object existingValue = attributes.get(name);
            if (existingValue == null) {
                attributes.put(name, value);
            }
            else {
                if (existingValue instanceof Collection) {
                    ((Collection<Object>) existingValue).add(value);
                }
                else {
                    final Collection<Object> values = new LinkedList<Object>();
                    values.add(existingValue);
                    values.add(values);
                    attributes.put(name, values);
                }
            }
        }
    }
}