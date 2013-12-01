package org.jeecqrs.event.dispatch;

/**
 * Provides the ability to dispatch events to interested listeners (publish-subscribe).
 * 
 * @param <E>   the base message type
 */
public interface EventDispatcher<E> {

    void dispatch(String bucketId, E event);
    
}
