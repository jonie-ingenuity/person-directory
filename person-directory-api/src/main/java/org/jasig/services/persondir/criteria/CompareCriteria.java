package org.jasig.services.persondir.criteria;


public abstract class CompareCriteria<V> implements Criteria {
    private final String attribute;
    private final V value;

    public CompareCriteria(String attribute, V value) {
        this.attribute = attribute;
        this.value = value;
    }
    
    public final String getAttribute() {
        return attribute;
    }

    public final V getValue() {
        return value;
    }

    @Override
    public Criteria getNegatedForm() {
        return new NotCriteria(this);
    }
}
