package org.jasig.services.persondir.criteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BinaryLogicCriteria implements Criteria {
    private final List<Criteria> queryList;
    private final LogicOperation operation;

    public BinaryLogicCriteria(LogicOperation operation, Criteria... queries) {
        this.operation = operation;
        
        if (queries.length < 1) {
            throw new IllegalArgumentException("At least one Criteria must be specified");
        }

        //TODO guava
        final ArrayList<Criteria> ql = new ArrayList<Criteria>(queries.length);
        for (final Criteria criteria : queries) {
            ql.add(criteria);
        }
        this.queryList = Collections.unmodifiableList(ql);
    }
    
    public BinaryLogicCriteria(LogicOperation operation, Collection<Criteria> queries) {
        this.operation = operation;
        
        if (queries.size() < 1) {
            throw new IllegalArgumentException("At least one Criteria must be specified");
        }

        //TODO guava
        final ArrayList<Criteria> ql = new ArrayList<Criteria>(queries);
        this.queryList = Collections.unmodifiableList(ql);
    }

    public final List<Criteria> getCriteriaList() {
        return queryList;
    }
    
    public final LogicOperation getOperation() {
        return operation;
    }

    public enum LogicOperation {
        AND {
            public LogicOperation getNegatedForm() {
                return OR;
            }
        },
        OR {
            public LogicOperation getNegatedForm() {
                return AND;
            }
        };
        
        public abstract LogicOperation getNegatedForm();
    }
}
