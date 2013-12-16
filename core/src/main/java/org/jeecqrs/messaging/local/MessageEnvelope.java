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
