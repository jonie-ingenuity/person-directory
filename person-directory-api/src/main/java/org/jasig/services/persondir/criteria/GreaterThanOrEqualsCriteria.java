package org.jasig.services.persondir.criteria;


public class GreaterThanOrEqualsCriteria extends ComparableCriteria {

    public GreaterThanOrEqualsCriteria(String attribute, Comparable<?> value) {
        super(attribute, value);
    }

    @Override
    protected boolean checkComparison(int compareResult) {
        return compareResult >= 0;
    }
    
    @Override
    public void process(CriteriaProcessor builder) {
        builder.appendGreaterThanOrEquals(this);
    }
}
