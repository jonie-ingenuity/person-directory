package org.jasig.services.persondir.util.attributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jasig.services.persondir.PersonAttributes;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 * {@link PersonAttributes} implementation where once constructed the attribute map cannot be modified
 */
public class ImmutablePersonAttributesImpl implements PersonAttributes {
    private final Map<String, List<Object>> attributes;
    
    public ImmutablePersonAttributesImpl(Map<String, ?> attributes) {
        final Map<String, List<Object>> attributesBuilder = new LinkedCaseInsensitiveMap<List<Object>>();
        
        for (final Map.Entry<String, ?> attributeEntry : attributes.entrySet()) {
            final String name = attributeEntry.getKey();
            
            final Object value = attributeEntry.getValue();
            if (value instanceof Collection<?>) {
                attributesBuilder.put(name, Collections.unmodifiableList(new ArrayList<Object>((Collection<?>)value)));
            }
            else {
                attributesBuilder.put(name, Collections.singletonList(value));
            }
            
        }
        
        this.attributes = Collections.unmodifiableMap(attributesBuilder);
    }
    
    @Override
    public Map<String, List<Object>> getAttributes() {
        return this.attributes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((attributes == null) ? 0 : attributes.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof PersonAttributes))
            return false;
        PersonAttributes other = (PersonAttributes) obj;
        if (attributes == null) {
            if (other.getAttributes() != null)
                return false;
        } else if (!attributes.equals(other.getAttributes()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PersonAttributes: " + attributes;
    }
}
