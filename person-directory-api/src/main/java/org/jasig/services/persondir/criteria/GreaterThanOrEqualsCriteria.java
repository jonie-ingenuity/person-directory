package org.jasig.services.persondir.criteria;


public class GreaterThanOrEqualsCriteria extends ComparableCriteria {

    public GreaterThanOrEqualsCriteria(String attribute, Comparable<?> value) {
        super(attribute, value);
    }

    @Override
    protected boolean compare(Comparable<Object> compareValue, Comparable<Object> attributeValue) {
        return compareValue.compareTo(attributeValue) <= 0;
    }
    
    @Override
    public void process(CriteriaProcessor builder) {
        builder.appendGreaterThanOrEquals(this);
    }
}
