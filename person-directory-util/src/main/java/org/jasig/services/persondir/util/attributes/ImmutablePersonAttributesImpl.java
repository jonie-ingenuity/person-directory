package org.jasig.services.persondir.util.attributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jasig.services.persondir.PersonAttributes;
import org.springframework.util.Assert;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 * {@link PersonAttributes} implementation where once constructed the attribute map cannot be modified
 */
public class ImmutablePersonAttributesImpl implements PersonAttributes {
    private final Map<String, List<Object>> attributes;
    
    /**
     * Should only be called if passing in a {@link LinkedCaseInsensitiveMap} that will have no external
     * references, this class will wrap it in an unmodifiable wrapper but not clone the original
     * map.
     * 
     * Generic usage should use {@link ImmutablePersonAttributesImpl#create(Map)}
     */
    protected ImmutablePersonAttributesImpl(LinkedCaseInsensitiveMap<List<Object>> attributes) {
        Assert.notNull(attributes, "attributes must not be null");
        this.attributes = Collections.unmodifiableMap(attributes);
    }
    
    /**
     * @return A new ImmutablePersonAttributesImpl based on the attributes
     */
    public static ImmutablePersonAttributesImpl create(Map<String, ?> attributes) {
        Assert.notNull(attributes, "attributes must not be null");
        
        final LinkedCaseInsensitiveMap<List<Object>> attributesBuilder = new LinkedCaseInsensitiveMap<List<Object>>();
        
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
        
        return new ImmutablePersonAttributesImpl(attributesBuilder);
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
        return attributes.toString();
    }
}
