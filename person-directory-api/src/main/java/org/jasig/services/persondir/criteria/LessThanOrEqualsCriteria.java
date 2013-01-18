package org.jasig.services.persondir.criteria;


public class LessThanOrEqualsCriteria extends ComparableCriteria {

    public LessThanOrEqualsCriteria(String attribute, Comparable<?> value) {
        super(attribute, value);
    }

    @Override
    protected boolean compare(Comparable<Object> compareValue, Comparable<Object> attributeValue) {
        return compareValue.compareTo(attributeValue) >= 0;
    }
    
    @Override
    public void process(CriteriaProcessor builder) {
        builder.appendLessThanOrEquals(this.getAttribute(), this.getValue());
    }
}
