package org.jasig.services.persondir.criteria;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AndCriteria extends BinaryLogicCriteria {
    
    public AndCriteria(Collection<Criteria> criterias) {
        super(criterias);
    }

    public AndCriteria(Criteria... criterias) {
        super(criterias);
    }
    
    AndCriteria(OrCriteria criteria) {
        super(criteria);
    }

    @Override
    public boolean equals(Map<String, List<Object>> attributes) {
        for (final Criteria criteria : this.getCriteriaList()) {
            if (!criteria.equals(attributes)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public BinaryLogicCriteria getNegatedForm() {
        return new OrCriteria(this);
    }
}
