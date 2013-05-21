package org.jasig.services.persondir.criteria;


public final class LessThanCriteria extends ComparableCriteria {

    public LessThanCriteria(String attribute, Comparable<?> value) {
        super(attribute, value);
    }
    
    @Override
    public CompareCriteria<Comparable<?>> getWithNewAttribute(String newAttribute) {
        return new LessThanCriteria(newAttribute, getValue());
    }

    @Override
    protected boolean checkComparison(int compareResult) {
        return compareResult < 0;
    }
    
    @Override
    public void process(CriteriaProcessor builder) {
        builder.appendLessThan(this);
    }
}
