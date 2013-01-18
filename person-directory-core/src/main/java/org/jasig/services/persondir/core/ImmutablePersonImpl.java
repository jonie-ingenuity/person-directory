package org.jasig.services.persondir.core;

import java.util.List;

import org.jasig.services.persondir.Person;
import org.jasig.services.persondir.util.attributes.ImmutablePersonAttributesImpl;
import org.springframework.util.LinkedCaseInsensitiveMap;

public final class ImmutablePersonImpl extends ImmutablePersonAttributesImpl implements Person {
    private final String primaryId;

    ImmutablePersonImpl(String primaryId, LinkedCaseInsensitiveMap<List<Object>> attributes) {
        super(attributes);
        this.primaryId = primaryId;
    }

    @Override
    public String getPrimaryId() {
        return this.primaryId;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((primaryId == null) ? 0 : primaryId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (!(obj instanceof Person))
            return false;
        Person other = (Person) obj;
        if (primaryId == null) {
            if (other.getPrimaryId() != null)
                return false;
        } else if (!primaryId.equals(other.getPrimaryId()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Person[" + primaryId + "] " + getAttributes();
    }
}
