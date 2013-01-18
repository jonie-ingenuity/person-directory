package org.jasig.services.persondir.core.worker;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import org.jasig.services.persondir.AttributeQuery;
import org.jasig.services.persondir.PersonAttributes;
import org.jasig.services.persondir.core.PersonBuilder;
import org.jasig.services.persondir.core.config.AttributeSourceConfig;
import org.jasig.services.persondir.criteria.Criteria;
import org.jasig.services.persondir.spi.BaseAttributeSource;

public interface AttributeQueryWorker<Q, C extends AttributeSourceConfig<? extends BaseAttributeSource>> {

    /**
     * Submit to the specified {@link ExecutorService} to execute
     * 
     * @throws IllegalStateException if {@link #submit(ExecutorService)} has already been called
     */
    void submit(ExecutorService service);

    /**
     * Get the result of the attribute query, must be called after {@link #submit(ExecutorService)}
     * 
     * @throws InterruptedException if interrupted while waiting for the result
     * @throws IllegalStateException if {@link #submit(ExecutorService)} has not been called yet
     */
    List<PersonAttributes> getResult() throws InterruptedException,
            TimeoutException;

    /**
     * @return The number of milliseconds to wait for the result from this worker
     */
    long getCurrentWaitTime();

    /**
     * @return The filtered query used by the worker
     */
    AttributeQuery<Q> getFilteredQuery();

    /**
     * @return The original query passed to the worker
     */
    AttributeQuery<Criteria> getOriginalQuery();

    /**
     * @return The time submitted, -1 if not yet submitted
     */
    long getSubmitted();

    /**
     * @return The time the query executed started, -1 if not yet started
     */
    long getStarted();

    /**
     * @return The time the query completed, -1 if not yet completed
     */
    long getComplete();

    PersonBuilder getPersonBuilder();

    boolean isComplete();

    boolean cancelFuture(boolean mayInterruptIfRunning);

    boolean isFutureCancelled();

    boolean isFutureDone();

    /**
     * @return The underlying attribute source configuration for this worker
     */
    C getSourceConfig();

}