package org.jasig.services.persondir.core.util;

import com.google.common.base.Function;

public final class NullFunction<F, T> implements Function<F, T> {
    private static final NullFunction<Object, Object> INSTANCE = new NullFunction<Object, Object>();
    
    @SuppressWarnings("unchecked")
    public static <F, T> NullFunction<F, T> instance() {
        return (NullFunction<F, T>) INSTANCE;
    }
    
    private NullFunction() {
    }
    
    public T apply(F input) {
        return null;
    }
}
