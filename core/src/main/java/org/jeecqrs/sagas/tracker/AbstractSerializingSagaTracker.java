package org.jeecqrs.sagas.tracker;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import org.jeecqrs.sagas.SagaTimeoutRequest;

public abstract class AbstractSerializingSagaTracker<E> extends AbstractSagaTracker<E> {

    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());
    
    @EJB(name="eventSerializer")
    private SagaTrackerEventSerializer serializer;

    @Override
    public void requestTimeout(SagaTimeoutRequest<E> request) {
	log.log(Level.FINE,
		"Schedule timeout in {0}ms for event {1}",
		new Object[]{request.timeout(), request.event().getClass().getSimpleName()});
        E event = request.event();
        String body = serialize(event);
        persist(request.sagaId(), request.description(),
                request.event().getClass().getCanonicalName(),
                body, request.timeout());
    }

    protected String serialize(E event) {
        return serializer.serialize(event);
    }

    protected abstract void persist(
            String sagaId,
            String description,
            String eventType,
            String eventBody,
            long timeout);

    protected void dispatch(String eventBody) {
        List<? extends E> events = this.serializer.deserialize(eventBody);
        for (E e : events)
            this.dispatch(e);
    }

}
