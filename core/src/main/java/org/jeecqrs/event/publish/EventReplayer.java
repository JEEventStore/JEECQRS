package org.jeecqrs.event.publish;

/**
 * Provides the ability to replay persisted events on application startup.
 * Implementations should be aware of long transactions and timeouts.
 */
public interface EventReplayer {

    void replayEvents();
    
}
