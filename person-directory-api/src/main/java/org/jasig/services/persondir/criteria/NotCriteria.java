package org.jasig.services.persondir.criteria;

public class NotCriteria implements Criteria {
    private final Criteria criteria;

    public NotCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public Criteria getCriteria() {
        return criteria;
    }
}
