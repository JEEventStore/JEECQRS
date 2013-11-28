package org.jeecqrs.messaging.local;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Wraps a message in an envelope for delivery.
 * 
 * @param <M>  the message type
 */
public class MessageEnvelope<M> {

    private final String topic;
    private final M message;

    public MessageEnvelope(String topic, M message) {
        this.topic = topic;
        this.message = message;
    }

    public String topic() {
        return topic;
    }

    public M message() {
        return message;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(topic)
            .append(message)
            .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null)
	    return false;
        if (!(obj instanceof MessageEnvelope))
            return false;
	final MessageEnvelope other = (MessageEnvelope) obj;
        return new EqualsBuilder()
                .append(topic, other.topic)
                .append(message, other.message)
                .isEquals();
    }
    
}
