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
package com.aerospike.play.sessionstore;

import java.util.Map;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import com.aerospike.session.CheckAndSetOperation;
import com.aerospike.session.SessionNotFound;
import com.aerospike.session.SessionStore;
import com.aerospike.session.SessionStoreException;

/**
 * Aerospike implementation of {@link AerospikePlaySessionStore}. The basic
 * requirement is the input configurations hostname, port, username, password,
 * namespace, configSet be correct.
 */
@RequiredArgsConstructor(onConstructor = @_(@Inject))
public class AerospikePlaySessionStoreimpl implements AerospikePlaySessionStore {

    private final SessionStore store;
    private final DefaultSessionIDProvider sessionIdProvider;

    /**
     * (non-Javadoc)
     *
     * @see com.aerospike.play.sessionstore.AerospikePlaySessionStore#put(java.lang.String,
     *      java.lang.Object)
     */
    @Override
    public void put(final String key, final Object value) {

        String sessionvalue = sessionIdProvider.getSessionId();
        store.put(sessionvalue, key, value);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.aerospike.play.sessionstore.AerospikePlaySessionStore#putAll(java.util.Map)
     */
    @Override
    public void putAll(final Map<String, Object> map) {
        String sessionvalue = sessionIdProvider.getSessionId();
        store.putAll(sessionvalue, map);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.aerospike.play.sessionstore.AerospikePlaySessionStore#get(java.lang.String)
     */
    @Override
    public Object get(final String key) {
        String sessionvalue = sessionIdProvider.getSessionId();
        try {
            return store.get(sessionvalue, key);
        } catch (SessionStoreException | SessionNotFound e) {
            throw new RuntimeException("Session expired/not found");
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.aerospike.play.sessionstore.AerospikePlaySessionStore#getAll()
     */
    @Override
    public Map<String, Object> getAll() {
        String sessionvalue = sessionIdProvider.getSessionId();
        try {
            return store.getAll(sessionvalue);
        } catch (SessionStoreException | SessionNotFound e) {
            throw new RuntimeException("Session expired/not found");
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.aerospike.play.sessionstore.AerospikePlaySessionStore#touch()
     */
    @Override
    public void touch() {
        String sessionvalue = sessionIdProvider.getSessionId();
        try {
            store.touch(sessionvalue);
        } catch (SessionStoreException | SessionNotFound e) {
            throw new RuntimeException("Session expired/not found");
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.aerospike.play.sessionstore.AerospikePlaySessionStore#destroy()
     */
    @Override
    public void destroy() {
        String sessionvalue = sessionIdProvider.getSessionId();
        store.destroy(sessionvalue);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.aerospike.play.sessionstore.AerospikePlaySessionStore#exists()
     */
    @Override
    public boolean exists() {
        String sessionvalue = sessionIdProvider.getSessionId();
        return store.exists(sessionvalue);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.aerospike.play.sessionstore.AerospikePlaySessionStore#checkAndSet(com.aerospike.session.CheckAndSetOperation)
     */
    @Override
    public void checkAndSet(CheckAndSetOperation addtocart) {
        String sessionvalue = sessionIdProvider.getSessionId();

        try {
            store.checkAndSet(sessionvalue, addtocart);
        } catch (SessionStoreException | SessionNotFound e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.aerospike.play.sessionstore.AerospikePlaySessionStore#create()
     */
    @Override
    public void create() {
        // add a dummy k,v to create aerospike record
        put("exists", true);
    }
}
