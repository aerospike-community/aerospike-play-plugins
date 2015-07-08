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

package com.aerospike.session.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.policy.GenerationPolicy;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.session.CheckAndSetOperation;
import com.aerospike.session.SessionIDProvider;
import com.aerospike.session.SessionNotFound;
import com.aerospike.session.SessionStore;
import com.aerospike.session.SessionStoreException;
import com.aerospike.transcoder.SessionStoreTranscoder;
import com.aerospike.transcoder.TranscodeException;
import com.aerospike.transcoder.Transcoder;

/**
 * Aerospike implementation of {@link SessionStore}. This implementation on
 * startup ensures that all application nodes running the session store share
 * the same {@link AerospikeSessionStoreConfig} by reading the configuration
 * from the aerospike cluster. The basic requirement is the input configurations
 * hostname, port, username, password, namespace, configSet be correct.
 *
 * @author ashish
 *
 */
@Slf4j
public class AerospikeSessionStore implements SessionStore {

    private final SessionIDProvider sessionIDProvider;
    /**
     * The session store configuration.
     */
    private final AerospikeSessionStoreConfig config;

    /**
     * The aerospike client.
     */
    private final AerospikeClient client;

    /**
     * Transcoder for pojos.
     */
    private final Transcoder transcoder;

    /**
     * @param config
     * @param client
     * @param transcoder
     */
    @Inject
    public AerospikeSessionStore(SessionIDProvider sessionIDProvider,
            AerospikeSessionStoreConfig config,
            @SessionStoreAerospikeClient AerospikeClient client,
            @SessionStoreTranscoder Transcoder transcoder) {
        this.sessionIDProvider = sessionIDProvider;
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
    private Object reformat(Object value, int depth) {
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
    private Object fetch(Object newValue) {
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

    /*
     * (non-Javadoc)
     *
     * @see com.aerospike.session.SessionStore#put(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public void put(final String key, final Object value)
            throws SessionStoreException {
        Object newValue = reformat(value, 0);
        log.debug("Adding new record");
        Key sessionID = new Key(config.getNamespace(), config.getSet(),
                sessionIDProvider.get());
        WritePolicy writePolicy = new WritePolicy();
        writePolicy.expiration = config.getSessionMaxAge();

        Bin bin1 = new Bin(key, newValue);
        client.put(writePolicy, sessionID, bin1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aerospike.session.SessionStore#putAll(java.util.Map)
     */
    @Override
    public void putAll(final Map<String, Object> map)
            throws SessionStoreException {

        log.debug("Adding new Records");
        Key sessionID = new Key(config.getNamespace(), config.getSet(),
                sessionIDProvider.get());
        ArrayList<Bin> binList = new ArrayList<Bin>();
        for (Entry<String, Object> entry : map.entrySet()) {
            Bin bin = new Bin(entry.getKey(), reformat(entry.getValue(), 0));
            binList.add(bin);
        }
        Bin[] bins = binList.toArray(new Bin[binList.size()]);
        WritePolicy writePolicy = new WritePolicy();
        writePolicy.expiration = config.getSessionMaxAge();
        client.put(writePolicy, sessionID, bins);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.aerospike.session.SessionStore#get(java.lang.String)
     */
    @Override
    public Object get(final String key) throws SessionNotFound,
            SessionStoreException {
        try {
            log.debug("Fetching given Record");
            WritePolicy writePolicy = new WritePolicy();
            writePolicy.expiration = config.getSessionMaxAge();
            writePolicy.recordExistsAction = RecordExistsAction.UPDATE_ONLY;
            Key sessionID = new Key(config.getNamespace(), config.getSet(),
                    sessionIDProvider.get());

            Record record = client.operate(writePolicy, sessionID,
                    Operation.touch(), Operation.get(key));

            if (record == null) {
                throw new SessionNotFound("Session not found");
            }
            return fetch(record.getValue(key));
        } catch (AerospikeException e) {
            throw new SessionStoreException("SessionStore Exception", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.aerospike.session.SessionStore#getAll()
     */
    @Override
    public Map<String, Object> getAll() throws SessionNotFound,
            SessionStoreException {
        try {
            log.debug("Fetching new records");
            WritePolicy writePolicy = new WritePolicy();
            writePolicy.expiration = config.getSessionMaxAge();
            writePolicy.recordExistsAction = RecordExistsAction.UPDATE_ONLY;
            Key sessionID = new Key(config.getNamespace(), config.getSet(),
                    sessionIDProvider.get());
            Record record = client.operate(writePolicy, sessionID,
                    Operation.touch(), Operation.get());
            if (record == null) {
                throw new SessionNotFound("Session not Found!");
            }
            return recordToMap(record);
        } catch (AerospikeException e) {
            throw new SessionNotFound("Sessionstore Exception");
        }
    }

    /**
     * Fetches records and returns Map of key value entries
     *
     * @param record
     *
     * @return
     */
    private Map<String, Object> recordToMap(Record record) {
        Map<String, Object> map = record.bins;
        Map<String, Object> fetchedmap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            fetchedmap.put(entry.getKey(), fetch(entry.getValue()));
        }
        return fetchedmap;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.aerospike.session.SessionStore#touch()
     */
    @Override
    public void touch() throws SessionNotFound, SessionStoreException {
        try {
            log.debug("Touch method");
            Key sessionID = new Key(config.getNamespace(), config.getSet(),
                    sessionIDProvider.get());
            WritePolicy writePolicy = new WritePolicy();
            writePolicy.expiration = config.getSessionMaxAge();
            writePolicy.recordExistsAction = RecordExistsAction.UPDATE_ONLY;
            if (client.get(writePolicy, sessionID) == null) {
                throw new SessionNotFound("Session Not Found!!");
            }
            client.touch(writePolicy, sessionID);
        } catch (AerospikeException e) {
            throw new SessionNotFound("Sessionstore Exception");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.aerospike.session.SessionStore#destroy()
     */
    @Override
    public void destroy() throws SessionStoreException {
        log.debug("Destroying the record");
        Key sessionID = new Key(config.getNamespace(), config.getSet(),
                sessionIDProvider.get());
        client.delete(null, sessionID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aerospike.session.SessionStore#exists()
     */
    @Override
    public boolean exists() throws SessionStoreException {
        log.debug("Checking for existence of the record");
        Key sessionID = new Key(config.getNamespace(), config.getSet(),
                sessionIDProvider.get());
        boolean itsHere = client.exists(new Policy(), sessionID);
        return itsHere;

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.aerospike.session.SessionStore#checkAndSet(com.aerospike.session.
     * CheckAndSetOperation)
     */
    @Override
    public void checkAndSet(CheckAndSetOperation sesOp)
            throws SessionStoreException, SessionNotFound {

        log.debug("Reading the contents of the record");
        Key sessionID = new Key(config.getNamespace(), config.getSet(),
                sessionIDProvider.get());
        for (int i = 0; i < config.getCheckAndSetMaxTries(); i++) {
            Record record = client.get(null, sessionID);
            Map<String, Object> curBins = recordToMap(record);
            Map<String, Object> newBins = sesOp.execute(curBins);
            WritePolicy writepolicy = new WritePolicy();
            writepolicy.expiration = config.getSessionMaxAge();
            writepolicy.recordExistsAction = RecordExistsAction.UPDATE;
            writepolicy.generationPolicy = GenerationPolicy.EXPECT_GEN_EQUAL;
            writepolicy.generation = record != null ? record.generation : 0;
            log.debug("{} {}", record, writepolicy.generation);
            ArrayList<Bin> binList = new ArrayList<Bin>();
            for (Entry<String, Object> entry : newBins.entrySet()) {
                Bin bin = new Bin(entry.getKey(), reformat(entry.getValue(), 0));
                binList.add(bin);
            }
            Bin[] bins = binList.toArray(new Bin[binList.size()]);
            try {
                client.put(writepolicy, sessionID, bins);
                break;
            } catch (AerospikeException e) {

            }

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aerospike.session.SessionStore#create()
     */
    @Override
    public void create() throws SessionStoreException {
        put("exists", true);

    }

}
