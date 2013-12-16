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

package org.jeecqrs.sagas.tracker.jpa;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import org.jeecqrs.sagas.tracker.AbstractPollingSagaTracker;

/**
 * Saga tracker that uses JPA to track timeout requests.
 * Deploy as Singleton bean.
 */
public class JPASagaTracker<E> extends AbstractPollingSagaTracker<E> {

    private static final Logger log = Logger.getLogger(JPASagaTracker.class.getCanonicalName());

    // injected by deployment descriptor
    private EntityManager entityManager;

    @Override
    protected void poll() {
        List<JPASagaTrackerEntry> list = entityManager
                .createQuery("FROM " +
			JPASagaTrackerEntry.class.getCanonicalName() +
			" e WHERE e.timeout <= :timeout",
			JPASagaTrackerEntry.class)
		.setParameter("timeout", new Date())
		.getResultList();
	for (JPASagaTrackerEntry entry : list) {
	    dispatch(entry);
	    entityManager.remove(entry);
	}
    }

    protected void dispatch(JPASagaTrackerEntry entry) {
        this.dispatch(entry.eventBody());
    }

    protected EntityManager entityManager() {
        return this.entityManager;
    }

    @Override
    protected void persist(String sagaId, String description, String eventType, String eventBody, long timeout) {
        log.log(Level.FINE,
		"Persisting timeout request in {0} ms for saga {1} and event {2}",
		new Object[]{ timeout, sagaId, eventType });
	Date timeoutDate = timeoutDate(timeout);
        JPASagaTrackerEntry entry = new JPASagaTrackerEntry(
                sagaId, description, eventType, eventBody, timeoutDate);
        entityManager().persist(entry);
        log.log(Level.FINE,
                "Persisted entry: {0} for {1}/{2}",
                new Object[] {
                    Long.toString(entry.id() == null ? -1 : entry.id()),
                    sagaId, eventType});
    }

}
