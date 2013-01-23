package org.jasig.services.persondir.criteria;

import java.util.List;
import java.util.Map;

public class NotCriteria implements Criteria {
    private final Criteria criteria;

    public NotCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    @Override
    public boolean matches(Map<String, List<Object>> attributes) {
        return !this.criteria.matches(attributes);
    }

    @Override
    public Criteria getNegatedForm() {
        return this.criteria;
    }
    
    @Override
    public void process(CriteriaProcessor builder) {
        builder.appendNotStart(this);
        this.criteria.process(builder);
        builder.appendNotEnd(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((criteria == null) ? 0 : criteria.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NotCriteria other = (NotCriteria) obj;
        if (criteria == null) {
            if (other.criteria != null)
                return false;
        } else if (!criteria.equals(other.criteria))
            return false;
        return true;
    }

    @Override
    public String toString() {
        final ToStringCriteriaProcessor processor = new ToStringCriteriaProcessor();
        this.process(processor);
        return processor.toString();
    }
}
