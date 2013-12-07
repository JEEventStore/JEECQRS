package org.jeecqrs.sagas.tracker;

import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import org.jeecqrs.sagas.SagaTimeoutRequest;
import org.jeecqrs.sagas.tracker.AbstractSerializingSagaTracker;

public abstract class AbstractPollingSagaTracker<E> extends AbstractSerializingSagaTracker<E> {

    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());
    
    @Resource
    private TimerService timerService;

    boolean disabled = true;

    @Override
    public void requestTimeout(SagaTimeoutRequest<E> request) {
        super.requestTimeout(request);
	if (request.timeout() < 5000)
	    scheduleEarlyCron(request.timeout() + 1);
    }

    protected abstract void poll();

    @Schedule(persistent = false, hour = "*", minute = "*", second = "*/5")
    @Lock(LockType.WRITE) // only one at a time
    public void cron() {
	if (disabled)
	    return;
        poll();
    }

    private void scheduleEarlyCron(long timeout) {
        TimerConfig config = new TimerConfig();
        config.setPersistent(false);
        timerService.createSingleActionTimer(timeout, config);
    }

    @Timeout
    public void scheduled(Timer timer) {
	cron();
    }

    @Override
    public void startPublication() {
        disabled = false;
        scheduleEarlyCron(0);
    }
    
}
