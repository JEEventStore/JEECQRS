package org.jeecqrs.events.bus.local;

/**
 * Translates between an event on the event bus and a topic in the multi
 * topic publisher messaging system using the event's canonical class name.
 * 
 * @param <E> the base event type
 */
public class DefaultTopicGenerationStrategy<E> implements TopicGenerationStrategy<E> {

    @Override
    public String topicFor(Class<? extends E> eventType) {
        return eventType.getCanonicalName();
    }

    @Override
    public String wildcardTopic() {
        return "*";
    }

}