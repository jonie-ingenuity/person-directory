package org.jasig.services.persondir.core.criteria;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.google.common.base.Function;

final class RandomFilter implements Function<String, String> {
    private final Random r = new Random(2);
    private final Set<String> included = new HashSet<String>();
    private final Set<String> excluded = new HashSet<String>();

    public String apply(String input) {
        if (included.contains(input)) {
            return input;
        }
        if (excluded.contains(input)) {
            return null;
        }
        if (r.nextBoolean()) {
            included.add(input);
            return input;
        }
        excluded.add(input);
        return null;
    }
}