package org.jeecqrs.sagas.tracker.jpa;

import javax.persistence.EntityManager;

public interface JPASagaTrackerPersistenceContextProvider {

    EntityManager entityManager();
    
}
