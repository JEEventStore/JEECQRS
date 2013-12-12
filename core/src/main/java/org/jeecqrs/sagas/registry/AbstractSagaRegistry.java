package org.jeecqrs.sagas.registry;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.ejb.Lock;
import javax.ejb.LockType;
import org.jeecqrs.sagas.Saga;
import org.jeecqrs.sagas.SagaRegistry;

public abstract class AbstractSagaRegistry<C, E> implements SagaRegistry<C, E> {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    private final Set<Class<? extends Saga<C, E>>> sagas = new HashSet<>();

    @Override
    @Lock(LockType.READ)
    public Set<Class<? extends Saga<C, E>>> allSagas() {
        return new HashSet<>(sagas);
    }

    /**
     * Must only be called from {@code LockType.WRITE} protected methods.
     */
    protected void register(Class<? extends Saga<C, E>> saga) {
        sagas.add(saga);
    }

}
