package org.jeecqrs.event.bus.local;

import org.jeecqrs.messaging.MultiTopicPublisher;

/**
 * Provides the ability to generate a {@link MultiTopicPublisher> topic
 * for a given event class.
 * 
 * @param <E>  the base event type
 */
public interface TopicGenerationStrategy<E> {

    /**
     * Gets the topic for the given event type.
     * 
     * @param eventType  the event type
     * @return  the topic name
     */
    String topicFor(Class<? extends E> eventType);

    /**
     * Gets the name of the wildcard topic containing all events published
     * on the event bus.
     * 
     * @return   the wildcard topic name
     */
    String wildcardTopic();
    
}
