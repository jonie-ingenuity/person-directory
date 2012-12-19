package org.jasig.services.persondir.criteria;


public class CompareCriteria implements Criteria {
    private final String attribute;
    private final CompareOperation operation;
    private final Object value;

    public CompareCriteria(String attribute, CompareOperation operation, Object value) {
        this.attribute = attribute;
        this.operation = operation;
        this.value = value;
    }

    public final String getAttribute() {
        return attribute;
    }
    
    public final CompareOperation getOperation() {
        return operation;
    }

    public final Object getValue() {
        return value;
    }
    
    public enum CompareOperation {
        EQUALS {
            public CompareOperation getNegatedForm() {
                return NOT_EQUALS;
            }
        },
        NOT_EQUALS {
            public CompareOperation getNegatedForm() {
                return EQUALS;
            }
        },
        LIKE {
            public CompareOperation getNegatedForm() {
                return NOT_LIKE;
            }
        },
        NOT_LIKE {
            public CompareOperation getNegatedForm() {
                return LIKE;
            }
        },
        GREATER_THAN {
            public CompareOperation getNegatedForm() {
                return LESS_THAN_OR_EQUALS;
            }
        },
        GREATER_THAN_OR_EQUALS {
            public CompareOperation getNegatedForm() {
                return LESS_THAN;
            }
        },
        LESS_THAN {
            public CompareOperation getNegatedForm() {
                return GREATER_THAN_OR_EQUALS;
            }
        },
        LESS_THAN_OR_EQUALS {
            public CompareOperation getNegatedForm() {
                return GREATER_THAN;
            }
        };
        
        public abstract CompareOperation getNegatedForm();
    }
}
