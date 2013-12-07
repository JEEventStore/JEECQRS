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

package org.jeecqrs.sagas;

/**
 * Provides the ability to load and save sagas.
 */
public interface SagaRepository {

    /**
     * Retrieves the saga with the given identity from the repository.
     * 
     * @param <T>  the type of the saga to load
     * @param clazz  the class of the saga to load
     * @param sagaId  the id of the saga to load
     * @return  the saga, or null if no such saga exists
     */
    <T extends Saga> T sagaOfIdentity(Class<T> clazz, String sagaId);

    /**
     * Adds the given saga to the repository.
     * 
     * @param <T>  the type of the saga
     * @param saga   the saga
     */
    <T extends Saga> void add(T saga);

    /**
     * Saves the given saga to the repository.
     * 
     * @param <T>  the type of the saga
     * @param saga   the saga
     */
    <T extends Saga> void save(T saga);

}
