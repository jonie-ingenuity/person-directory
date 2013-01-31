package org.jasig.services.persondir.criteria;


public abstract class BaseCriteria implements Criteria {

    @Override
    public final String toString() {
        final ToStringCriteriaProcessor processor = new ToStringCriteriaProcessor();
        this.process(processor);
        return processor.toString();
    }
}
