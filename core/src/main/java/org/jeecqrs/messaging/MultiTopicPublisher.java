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

package org.jeecqrs.messaging;

/**
 * Publish-subscribe messaging system publisher supporting multiple topics
 * per subscriber.
 * Implementations must be thread-safe.
 * 
 * @param <M>  the message interface
 */
public interface MultiTopicPublisher<M> {

    /**
     * Publishes a {@code message} in a given {@code topic}.
     * 
     * @param topic    the topic the message
     * @param message  the message to be published
     */
    void publish(String topic, M message);

    /**
     * Subscribes an interested listener.
     * 
     * @param subscriber  the subscriber, not null, not subscribed already
     */
    void subscribe(MultiTopicSubscriber<M> subscriber);

    /**
     * Unsubscribes a subscriber.
     * 
     * @param subscriber  the subscriber to unsubscribe, not null, subscribed
     */
    void unsubscribe(MultiTopicSubscriber<M> subscriber);
    
}
