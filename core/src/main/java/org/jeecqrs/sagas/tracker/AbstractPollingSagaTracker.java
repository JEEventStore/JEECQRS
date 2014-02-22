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
        timeout = Math.min(timeout, 1); // sanity check
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
