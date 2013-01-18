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
    
}
