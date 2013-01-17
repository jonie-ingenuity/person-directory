package org.jasig.services.persondir.criteria;

import java.util.LinkedList;
import java.util.List;

import org.jasig.services.persondir.criteria.BinaryLogicCriteria.LogicOperation;

public class LogicCriteriaBuilder {
    private final List<Criteria> criterias = new LinkedList<Criteria>();
    private final LogicOperation logicOperation;

    LogicCriteriaBuilder(LogicOperation logicOperation) {
        this.logicOperation = logicOperation;
    }
    
    public LogicCriteriaBuilder add(Criteria c) {
        this.criterias.add(c);
        return this;
    }
    
    public LogicCriteriaBuilder addAll(Criteria... cs) {
        for (final Criteria c : cs) {
            this.criterias.add(c);
        }
        return this;
    }
    
    public LogicCriteriaBuilder addAll(Iterable<Criteria> cs) {
        for (final Criteria c : cs) {
            this.criterias.add(c);
        }
        return this;
    }
    
    public BinaryLogicCriteria build() {
        return new BinaryLogicCriteria(this.logicOperation, this.criterias);
    }
}
