package org.jeecqrs.startup;

import javax.annotation.PostConstruct;

/**
 * Orchestrates the initialization of the various infrastructure services.
 * Deploy as @Startup @Singleton EJB.
 * <p>
 * Contrary to the other services, the interface of {@link AbstractApplicationStartup}
 * does not contain the notion of a "bucket", since it is meant as a per-module
 * initialization.
 */
public abstract class AbstractApplicationStartup {

    @PostConstruct
    public void startup() {
        wireUpEventListeners();
        wireUpCommandHandlers();
        wireUpDispatchScheduler();
        wireUpSagaTracker();
        replayEvents();
        startDispatchScheduler();
        startSagaTracker();
    }

    /**
     * Registers event handlers at the {@link EventDispatcher}.
     */
    protected abstract void wireUpEventListeners();

    /**
     * Registers command handlers at the {@link CommandBus}.
     */
    protected abstract void wireUpCommandHandlers();

    /**
     * Sets up the bridge between the {@link EventDispatcher} and the
     * persistence layer, such that newly persisted events are continuously
     * published to the dispatcher.
     * The preferred way is to hook into the event persistence receive
     * notifications about persisted events asynchronously once the events
     * have been persisted to the durable storage. 
     * If this is not supported by the persistence technology, implementations
     * may also use other techniques, such as polling in fixed intervals to
     * query a database for recently published events.
     */
    protected abstract void wireUpDispatchScheduler();

    /**
     * Sets up the {@link SagaTracker} to handle timed events in sagas.
     */
    protected abstract void wireUpSagaTracker();

    /**
     * Replay persisted events on application startup.
     * Implementations should be aware of long transactions and timeouts.
     */
    protected abstract void replayEvents();

    /**
     * Starts the {@link DispatchScheduler}, if required (asynchronous
     * implementations may not need this).
     */
    protected abstract void startDispatchScheduler();

    /**
     * Starts the {@link SagaTracker}, if required (asynchronous
     * implementations may not need this).
     */
    protected abstract void startSagaTracker();

}
