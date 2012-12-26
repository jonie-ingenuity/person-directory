package org.jasig.services.persondir.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;

/**
 * Base class for fluent configuration builders.
 * 
 * @author Eric Dalquist
 * @version $Revision$
 * @param <T> The type returned by fluent methods, must be an interface and the concrete superclass must implement it
 */
abstract class AbstractConfigBuilder<T> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final Class<T> type;
    private BeanFactory beanFactory;

    public AbstractConfigBuilder(Class<T> type) {
        Assert.notNull(type, "type cannot be null");
        
        //Sanity checks to make sure the builder type hierarchy is correct
        if (!type.isInterface()) {
            throw new Error(this.getClass().getName() + " has an illegal generics configuration. The generic type " + type + " must be an interface");
        }
        if (!type.isAssignableFrom(this.getClass())) {
            throw new Error(this.getClass().getName() + " has an illegal generics configuration. This class " + this.getClass().getName() + " must implement " + type);
        }
        
        this.type = type;
    }

    /**
     * Return a reference to this cast to the generic type T. Useful shortcut
     * as a return for fluent methods.
     * 
     * @return typed reference to this class
     */
    protected final T getThis() {
        return this.type.cast(this);
    }

    /**
     * To be called when initial configuration is complete. Resolves any required beans
     * from the {@link BeanFactory} and may make some of th configuration options read-only
     */
    protected final void resolveConfiguration(BeanFactory beanFactory) {
        Assert.notNull(beanFactory, "beanFactory must not be null");
        
        this.beanFactory = beanFactory;
        this.doResolveConfiguration();
    }

    /**
     * @return The {@link BeanFactory} after {@link #resolveConfiguration(BeanFactory)} has been called, null before.
     */
    protected final BeanFactory getBeanFactory() {
        return beanFactory;
    }

    /**
     * For subclasses that wish to be notified after {@link #resolveConfiguration(BeanFactory)} has been
     * called. 
     */
    protected void doResolveConfiguration() {
    }

}