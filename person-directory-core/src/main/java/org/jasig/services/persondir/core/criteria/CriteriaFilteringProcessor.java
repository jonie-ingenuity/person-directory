package org.jasig.services.persondir.core.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.jasig.services.persondir.criteria.AndCriteria;
import org.jasig.services.persondir.criteria.BaseCriteriaProcessor;
import org.jasig.services.persondir.criteria.BinaryLogicCriteria;
import org.jasig.services.persondir.criteria.CompareCriteria;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.criteria.NotCriteria;
import org.jasig.services.persondir.criteria.OrCriteria;

import com.google.common.base.Function;

/**
 * Filters a Criteria tree with the ability to selectively remove elements as well
 * as simplify the criteria tree in the case of and/or logic that contains only a
 * single sub-criteria 
 * 
 * @author Eric Dalquist
 */
public final class CriteriaFilteringProcessor extends BaseCriteriaProcessor {
    private Criteria rootCriteria = null;
    private final Stack<List<Criteria>> logicBuilders = new Stack<List<Criteria>>();
    private final Function<String, Boolean> attributeFilter;
    
    public CriteriaFilteringProcessor(Function<String, Boolean> attributeFilter) {
        this.attributeFilter = attributeFilter;
    }
    
    public Criteria getRootCriteria() {
        return rootCriteria;
    }

    @Override
    public void appendBinaryLogicStart(BinaryLogicCriteria criteria) {
        this.logicBuilders.push(new ArrayList<Criteria>(criteria.getCriteriaList().size()));
    }

    @Override
    public void appendAndEnd(AndCriteria criteria) {
        final List<Criteria> logicBuilder = this.logicBuilders.pop();

        if (logicBuilder.size() == 1) {
            appendNewCriteria(logicBuilder.get(0));
        }
        else if (!logicBuilder.isEmpty()) {
            final AndCriteria logicCriteria = new AndCriteria(logicBuilder);
            appendNewCriteria(logicCriteria);
        }
    }

    @Override
    public void appendOrEnd(OrCriteria criteria) {
        final List<Criteria> logicBuilder = this.logicBuilders.pop();

        if (logicBuilder.size() == 1) {
            appendNewCriteria(logicBuilder.get(0));
        }
        else if (!logicBuilder.isEmpty()) {
            final OrCriteria logicCriteria = new OrCriteria(logicBuilder);
            appendNewCriteria(logicCriteria);
        }
    }

    @Override
    public void appendNotStart(NotCriteria criteria) {
        this.logicBuilders.push(new ArrayList<Criteria>(1));
    }
    
    @Override
    public void appendNotEnd(NotCriteria criteria) {
        final List<Criteria> notChildCriteria = this.logicBuilders.pop();
        
        if (notChildCriteria.size() == 1) {
            appendNewCriteria(new NotCriteria(notChildCriteria.get(0)));
        }
        else if (!notChildCriteria.isEmpty()) {
            throw new IllegalArgumentException("NotCriteria had " + notChildCriteria.size() + " children");
        }
    }

    @Override
    public void appendCompare(CompareCriteria<?> criteria) {
        if (this.attributeFilter.apply(criteria.getAttribute())) {
            appendNewCriteria(criteria);
        }
    }

    private void appendNewCriteria(Criteria logicCriteria) {
        final List<Criteria> parentLogicBuilder = getParentLogicBuilder();
        if (parentLogicBuilder != null) {
            parentLogicBuilder.add(logicCriteria);
        }
        else if (this.rootCriteria != null) {
            throw new IllegalArgumentException("Criteria tree resulted in two roots");
        }
        else {
            this.rootCriteria = logicCriteria;
        }
    }
    
    private List<Criteria> getParentLogicBuilder() {
        if (this.logicBuilders.isEmpty()) {
            return null;
        }
        
        return this.logicBuilders.peek();
    }
}