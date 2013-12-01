package org.jeecqrs.startup;

import javax.annotation.PostConstruct;

/**
 * Orchestrates the initialization of the various infrastructure services.
 * Deploy as @Startup @Singleton EJB.
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
     * persistence layer, such that newly persisted events are published
     * to the dispatcher.
     * Implementations might use polling or use an asynchronous listener
     * if the persistence implementations supports this.
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
