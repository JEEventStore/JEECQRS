/*
 * Copyright (c) 2013 Red Rainbow IT Solutions GmbH, Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.jeecqrs.startup;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 * Orchestrates the initialization of the various infrastructure services.
 * Deploy as @Startup @Singleton EJB.
 * <p>
 * Contrary to the other services, the interface of {@link AbstractApplicationStartup}
 * does not contain the notion of a "bucket", since it is meant as a per-module
 * initialization.
 */
public abstract class AbstractApplicationStartup {

    private final static Logger log = Logger.getLogger(AbstractApplicationStartup.class.getSimpleName());

    @Resource(name="transactionTimeout")
    private int transactionTimeout = 84000;

    @Resource(name="startupDelay")
    private int startupDelay = 5000;

    @Resource
    private UserTransaction userTransaction;

    @Resource
    private TimerService timerService;

    @PostConstruct
    public void startup() {
        try {
            userTransaction.setTransactionTimeout(transactionTimeout);
        } catch (SystemException ex) {
            Logger.getLogger(AbstractApplicationStartup.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            userTransaction.begin();
            wireUpEventListeners();
            wireUpCommandHandlers();
            wireUpDispatchScheduler();
            wireUpSagaTracker();
            scheduleStartup();
            userTransaction.commit();
        } catch (NotSupportedException | SystemException | HeuristicMixedException |
                HeuristicRollbackException | IllegalStateException | RollbackException |
                SecurityException ex) {
            // all of these are hard errors that prevent application startup
            throw new RuntimeException(ex);
        }
    }

    private void scheduleStartup() {
        TimerConfig config = new TimerConfig();
        config.setPersistent(false);
        timerService.createSingleActionTimer(startupDelay, config);
    }

    @Timeout
    public void startup(Timer timer) {
        try {
            userTransaction.begin();
            log.log(Level.INFO, "Starting application...");
            replayEvents();
            startDispatchScheduler();
            startSagaTracker();
            userTransaction.commit();
        } catch (NotSupportedException | SystemException | HeuristicMixedException |
                HeuristicRollbackException | IllegalStateException | RollbackException |
                SecurityException ex) {
            log.severe(ex.getMessage());
            throw new RuntimeException(ex);
        }
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
     * Starts the dispatch scheduler, if required (asynchronous
     * implementations may not need this).
     */
    protected abstract void startDispatchScheduler();

    /**
     * Starts the {@link SagaTracker}, if required (asynchronous
     * implementations may not need this).
     */
    protected abstract void startSagaTracker();

}
