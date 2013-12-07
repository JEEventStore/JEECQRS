package org.jeecqrs.sagas.tracker;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
import javax.ejb.EJB;
import org.jeecqrs.event.EventBus;
import org.jeecqrs.sagas.SagaTracker;

public abstract class AbstractSagaTracker<E> implements SagaTracker<E> {

    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());
    
    // beware! should be an event bus that delivers to a persistence layer AND should use persistent queues
    // to avoid lost events
    @EJB(name="eventBus")
    private EventBus<E> eventBus;

    /**
     * Dispatches the timed event to the event bus to make it available
     * to the persistence store.
     * 
     * @param event 
     */
    protected void dispatch(E event) {
        eventBus.dispatch(event);
    }

    protected Date timeoutDate(long timeout) {
        int span = (int) timeout;
        if (timeout != span)
            throw new IllegalStateException(
                    "Timespan too big, integer overflow in Java's Calendar: " + timeout + " != " + span);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, span);
        return cal.getTime();
    }

}
