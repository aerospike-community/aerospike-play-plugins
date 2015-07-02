/*
 * Copyright 2008-2015 Aerospike, Inc.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.transcoder.CacheTranscoder;
import com.aerospike.transcoder.TranscodeException;
import com.aerospike.transcoder.Transcoder;

/**
 * Aerospike Implementation of Cache
 *
 * @author akshay
 *
 */
@Slf4j
public class AerospikeCache {

    private final AerospikeCacheConfig config;
    private final AerospikeClient client;
    private final Transcoder transcoder;

    /**
     * Instantiate the cache.
     *
     * @param config
     * @param client
     * @param transcoder
     */
    @Inject
    public AerospikeCache(AerospikeCacheConfig config,
            @CacheAerospikeClient AerospikeClient client,
            @CacheTranscoder Transcoder transcoder) {
        super();
        this.config = config;
        this.client = client;
        this.transcoder = transcoder;
    }

    /**
     * The method to check whether the value is an instance of primitive
     * datatype. If not, we use transcoder to handle pojos.
     *
     * @param value
     * @param depth
     * @return
     */
    @SuppressWarnings("unchecked")
    public Object reformat(Object value, int depth) {
        log.debug("Transcoder depth" + depth);
        try {
            if (value instanceof String || value instanceof Number
                    || value instanceof Boolean) {
                return value;
            } else if (depth == 0 && value instanceof List<?>) {
                List<Object> newList = new ArrayList<Object>();
                for (Object obj : (List<?>) value) {
                    newList.add(reformat(obj, depth + 1));

                }
                return newList;
            } else if (depth == 0 && value instanceof Map) {
                Map<Object, Object> newMap = new HashMap<Object, Object>();
                for (Entry<Object, Object> entry : ((Map<Object, Object>) value)
                        .entrySet()) {
                    Object newKey = reformat(entry.getKey(), depth + 1);
                    Object newVal = reformat(entry.getValue(), depth + 1);
                    newMap.put(newKey, newVal);
                }
                return newMap;
            } else if (value instanceof byte[]) {

                /**
                 *
                 * Using the convention: If the value given by the user is
                 * bytearray we just prepend '0'
                 */
                byte[] newValue = new byte[1 + ((byte[]) value).length];
                newValue[0] = 0;
                System.arraycopy(value, 0, newValue, 1, newValue.length - 1);
                return newValue;
            } else {

                byte[] enValue;
                enValue = transcoder.encode(value);
                byte[] newValue = new byte[1 + enValue.length];
                newValue[0] = 1;
                System.arraycopy(enValue, 0, newValue, 1, newValue.length - 1);
                return newValue;

            }
        } catch (IOException e) {
            throw new TranscodeException(
                    "ERROR:error occured during Transcoding", e);
        }

    }

    /**
     * The method to identify whether the retrieved object has a primitive
     * datatype. Else we use Transcoder to get back encode/decode objects
     *
     *
     * @param newValue
     * @return
     */
    @SuppressWarnings("unchecked")
    public Object fetch(Object newValue) {
        if (newValue == null) {
            return null;
        } else if (newValue instanceof String) {
            return newValue;
        } else if (newValue instanceof Number) {
            return newValue;
        } else if (newValue instanceof Boolean) {
            return newValue;
        } else if (newValue instanceof List<?>) {
            List<Object> newList = new ArrayList<Object>();
            for (Object obj : (List<?>) newValue) {
                newList.add(fetch(obj));

            }
            return newList;

        } else if (newValue instanceof Map) {
            Map<Object, Object> newMap = new HashMap<Object, Object>();
            for (Entry<Object, Object> entry : ((Map<Object, Object>) newValue)
                    .entrySet()) {
                Object newKey = fetch(entry.getKey());
                Object newVal = fetch(entry.getValue());
                newMap.put(newKey, newVal);
            }
            return newMap;
        } else if (newValue instanceof byte[]) {
            byte enValue = ((byte[]) newValue)[0];
            if (enValue == 0) {
                byte[] value = new byte[((byte[]) newValue).length - 1];
                System.arraycopy(newValue, 1, value, 0, value.length);
                return value;
            } else {
                byte[] value = new byte[((byte[]) newValue).length - 1];
                System.arraycopy(newValue, 1, value, 0, value.length);
                return transcoder.decode(value);
            }

        } else {
            log.error("Unknown type {}", newValue);
            throw new RuntimeException();
        }
    }

    /**
     * Set up the cache value
     *
     * @param key
     * @param Value
     * @param expiration
     */
    public void set(String key, Object Value, int expiration) {
        Object newValue = reformat(Value, 0);
        log.debug("Storing in cache");
        Key cacheKey = new Key(config.getNamespace(), config.getSet(), key);
        WritePolicy writepolicy = new WritePolicy();
        writepolicy.expiration = expiration;
        Bin bin = new Bin(config.getBin(), newValue);
        client.put(writepolicy, cacheKey, bin);
    }

    /**
     * Get the value from the cache
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        Key cacheKey = new Key(config.getNamespace(), config.getSet(), key);
        Record record = client.get(null, cacheKey);
        try {
            return fetch(record.getValue(config.getBin()));
        } catch (NullPointerException e) {
            throw new RuntimeException("Requested item not found in cache!");
        }
    }

    /**
     * Get the value if it exists from the cache, else set the value and return
     *
     * @param key
     * @param block
     * @param expiration
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrElse(String key, Callable<T> block, int expiration) {
        Key cacheKey = new Key(config.getNamespace(), config.getSet(), key);
        Record record = client.get(null, cacheKey);
        if (record == null) {
            log.debug("Value not found in cache! Caching value");
            try {
                set(key, block.call(), expiration);
            } catch (Exception e1) {
                throw new RuntimeException("Could no set Cache value{}");
            }
            record = client.get(null, cacheKey);
            return (T) fetch(record.getValue(config.getBin()));
        } else {
            log.debug("Retrieving value from the cache");
            return (T) fetch(record.getValue(config.getBin()));
        }
    }

    /**
     * Clear the cache
     * 
     * @param key
     */
    public void remove(String key) {
        log.debug("Clearing Cache");
        Key cacheKey = new Key(config.getNamespace(), config.getSet(), key);
        client.delete(null, cacheKey);
    }
}