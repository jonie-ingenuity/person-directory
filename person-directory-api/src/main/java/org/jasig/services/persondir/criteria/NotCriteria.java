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
    public boolean equals(Map<String, List<Object>> attributes) {
        return !this.criteria.equals(attributes);
    }

    @Override
    public Criteria getNegatedForm() {
        return this.criteria;
    }
}
