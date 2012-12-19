package org.jasig.services.persondir.criteria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BinaryLogicCriteria implements Criteria {
    private final List<Criteria> queryList;
    private final LogicOperation operation;

    public BinaryLogicCriteria(LogicOperation operation, Criteria query, Criteria... queries) {
        this.operation = operation;

        //TODO guava
        final ArrayList<Criteria> ql = new ArrayList<Criteria>(queries.length + 1);
        ql.add(query);
        for (final Criteria criteria : queries) {
            ql.add(criteria);
        }
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
