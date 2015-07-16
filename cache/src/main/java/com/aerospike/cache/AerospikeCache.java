/*
 * Copyright (C) 2008-2015 Aerospike, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aerospike.cache;

import java.util.concurrent.Callable;

/**
 * Aerospike Implementation of Cache.
 *
 * @author akshay
 *
 */
public interface AerospikeCache {

    /**
     * For the given key, set up the value in cache
     *
     * @param key
     * @param Value
     * @param expiration
     *            any non-zero value in seconds. Use -1 for infinity.
     *
     */
    void set(String key, Object Value, int expiration);

    /**
     * Get the value from the cache for given key.
     *
     * @param key
     * @return value if found, else null.
     */
    Object get(String key);

    /**
     * Get the value for given key if it exists from the cache, else invoke the
     * callable set the result in cache and return the value.
     *
     * @param key
     * @param callable
     * @param expiration
     * @return
     * @throws Exception
     *             only if the callable failed.
     */
    <T> T getOrElse(String key, Callable<T> callable, int expiration)
            throws Exception;

    /**
     * Remove the key-value from the cache
     *
     * @param key
     */
    void remove(String key);

}