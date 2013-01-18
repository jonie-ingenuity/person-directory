package org.jasig.services.persondir.criteria;


public class LessThanCriteria extends ComparableCriteria {

    public LessThanCriteria(String attribute, Comparable<?> value) {
        super(attribute, value);
    }

    @Override
    protected boolean compare(Comparable<Object> compareValue, Comparable<Object> attributeValue) {
        return compareValue.compareTo(attributeValue) > 0;
    }
    
    @Override
    public void process(CriteriaProcessor builder) {
        builder.appendLessThan(this.getAttribute(), this.getValue());
    }
}
