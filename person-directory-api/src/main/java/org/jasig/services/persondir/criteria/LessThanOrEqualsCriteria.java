package org.jasig.services.persondir.criteria;


public final class LessThanOrEqualsCriteria extends ComparableCriteria {

    public LessThanOrEqualsCriteria(String attribute, Comparable<?> value) {
        super(attribute, value);
    }
    
    @Override
    public CompareCriteria<Comparable<?>> getWithNewAttribute(String newAttribute) {
        return new LessThanOrEqualsCriteria(newAttribute, getValue());
    }

    @Override
    protected boolean checkComparison(int compareResult) {
        return compareResult <= 0;
    }
    
    @Override
    public void process(CriteriaProcessor builder) {
        builder.appendLessThanOrEquals(this);
    }
}
