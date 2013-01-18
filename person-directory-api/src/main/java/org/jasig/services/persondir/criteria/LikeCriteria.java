package org.jasig.services.persondir.criteria;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * An astrisk is used as the wildcard character for like comparisons
 */
public class LikeCriteria extends CompareCriteria<String> {
    private static final Pattern WILDCARD = Pattern.compile(Pattern.quote("*"));

    public LikeCriteria(String attribute, String value) {
        super(attribute, value);
    }

    @Override
    public boolean equals(Map<String, List<Object>> attributes) {
        final String compareValue = this.getValue();
        final Pattern comparePattern = Pattern.compile(WILDCARD.matcher(compareValue).replaceAll(".*"));
        
        final List<Object> values = attributes.get(this.getAttribute());
        
        for (final Object value : values) {
            if (comparePattern.matcher(value.toString()).matches()) {
                return true;
            }
        }
        
        return false;
    }
}
