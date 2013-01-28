package org.jasig.services.persondir.criteria;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * {@link #PATTERN} is used as the wildcard character for like comparisons
 */
public class LikeCriteria extends CompareCriteria<String> {
    public static final String PATTERN = "*";
    private static final Pattern WILDCARD_PATTERN = Pattern.compile(Pattern.quote(PATTERN));
    
    private Pattern valuePattern;

    public LikeCriteria(String attribute, String value) {
        super(attribute, value);
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
    }
    
    private Pattern getValuePattern() {
        if (this.valuePattern != null) {
            return this.valuePattern;
        }
        
        final String compareValue = this.getValue();
        this.valuePattern = Pattern.compile(WILDCARD_PATTERN.matcher(compareValue).replaceAll(".*"));
        
        return this.valuePattern;
    }

    @Override
    public boolean matches(Map<String, List<Object>> attributes) {
        final List<Object> attrValues = attributes.get(this.getAttribute());
        if (attrValues == null || attrValues.isEmpty()) {
            return false;
        }
        
        final Pattern valuePattern = getValuePattern();
        for (final Object attrValue : attrValues) {
            if (attrValue != null && valuePattern.matcher(attrValue.toString()).matches()) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public void process(CriteriaProcessor builder) {
        builder.appendLike(this);
    }
}
