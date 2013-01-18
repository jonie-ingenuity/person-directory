package org.jasig.services.persondir.criteria;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class OrCriteria extends BinaryLogicCriteria {
    
    public OrCriteria(Collection<Criteria> criterias) {
        super(criterias);
    }

    public OrCriteria(Criteria... criterias) {
        super(criterias);
    }
    
    public OrCriteria(AndCriteria criteria) {
        super(criteria);
    }

    @Override
    public boolean equals(Map<String, List<Object>> attributes) {
        for (final Criteria criteria : this.getCriteriaList()) {
            if (criteria.equals(attributes)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BinaryLogicCriteria getNegatedForm() {
        return new AndCriteria(this);
    }
}
