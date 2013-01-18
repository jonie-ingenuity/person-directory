package org.jasig.services.persondir.criteria;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

public abstract class BinaryLogicCriteria implements Criteria {
    private final List<Criteria> criteriaList;

    public BinaryLogicCriteria(Criteria... criterias) {
        if (criterias.length < 1) {
            throw new IllegalArgumentException("At least one Criteria must be specified");
        }

        this.criteriaList = ImmutableList.copyOf(criterias);
    }
    
    public BinaryLogicCriteria(Collection<Criteria> criterias) {
        if (criterias.size() < 1) {
            throw new IllegalArgumentException("At least one Criteria must be specified");
        }

        this.criteriaList = ImmutableList.copyOf(criterias);
    }
    
    BinaryLogicCriteria(BinaryLogicCriteria criteria) {
        this.criteriaList = criteria.criteriaList;
    }

    public final List<Criteria> getCriteriaList() {
        return criteriaList;
    }

    @Override
    public abstract BinaryLogicCriteria getNegatedForm();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((criteriaList == null) ? 0 : criteriaList.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BinaryLogicCriteria other = (BinaryLogicCriteria) obj;
        if (criteriaList == null) {
            if (other.criteriaList != null)
                return false;
        } else if (!criteriaList.equals(other.criteriaList))
            return false;
        return true;
    }

    @Override
    public String toString() {
        final ToStringCriteriaProcessor processor = new ToStringCriteriaProcessor();
        this.process(processor);
        return processor.toString();
    }
}
