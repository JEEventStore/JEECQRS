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

import java.util.Set;

/**
 * Publish-subscribe messaging system subscriber supporting multiple topics
 * of interest.
 * 
 * @param <M>   the message interface
 */
public interface MultiTopicSubscriber<M> {

    /**
     * Receives a new message in the given topic.
     * Called upon arrival of a new topic message.
     * Must be thread-safe.
     * 
     * @param topic    the topic the message was published in
     * @param message  the message
     */
    void receiveMessage(String topic, M message);

    /**
     * The publisher has canceled the subscription of this subscriber.
     * Under circumstances the publisher might want to force a detach
     * of a subscriber, for example when the number of retries to deliver a
     * message has been exhausted.  This method is called after the
     * publisher has canceled the subscription and may be used by the client
     * to handle the situation gracefully.
     */
    void canceledSubscription();

    /**
     * Gets the set of topics this subscriber is interested in.
     * Must not change between subscription and unsubscription.
     * 
     * @return  the topics of interest
     */
    Set<String> interestedInTopics();
    
}
