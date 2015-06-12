/*
 * Copyright (C) 2015 Aeroshift Authors
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
import java.util.HashMap;
import java.util.Map;

import lombok.Cleanup;

import org.junit.Assert;
import org.junit.Test;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.session.SessionNotFound;
import com.aerospike.session.SessionStoreException;

/**
 * @author akshay
 *
 */
public class AerospikeSessiontimeoutTest {
    /*
     * Test method for defaultTTL. The expiration value will be read from
     * Aerospike.conf
     */
    @Test
    public void defaultTTLtest() throws SessionStoreException, SessionNotFound,
            IOException, InterruptedException {
        ConfigReader configReader = new ConfigReader();
        AerospikeSessionStoreConfig config = configReader
                .getConfiguration("aeroshift_defaultTTL.cfg");
        AerospikeClientProvider clientProvider = new AerospikeClientProvider(
                config);
        AerospikeSessionStore store = new AerospikeSessionStore(config,
                clientProvider.get(), null);
        store.put("default_session", "name", "John Doe");
        store.put("default_session", "roll_no", 21);
        Thread.sleep(10000);
        Assert.assertEquals("John Doe", store.get("default_session", "name"));
    }

    /*
     * Test method for smallTTL when we read before the session expires
     */
    @Test
    public void smallTTLpasstest() throws SessionStoreException,
            SessionNotFound, IOException, InterruptedException {
        ConfigReader configReader = new ConfigReader();
        AerospikeSessionStoreConfig config = configReader
                .getConfiguration("aeroshift_smallTTL.cfg");
        AerospikeClientProvider clientProvider = new AerospikeClientProvider(
                config);
        AerospikeSessionStore store = new AerospikeSessionStore(config,
                clientProvider.get(), null);
        store.put("small_session", "name", "Jane Doe");
        store.put("small_session", "roll_no", 21);
        Thread.sleep(5000);
        Assert.assertEquals("Jane Doe", store.get("small_session", "name"));
    }

    /*
     * Test method for smallTTL when we read after the session expires
     */
    @Test
    public void smallTTLfailtest() throws SessionStoreException,
            SessionNotFound, IOException, InterruptedException {
        ConfigReader configReader = new ConfigReader();
        AerospikeSessionStoreConfig config = configReader
                .getConfiguration("aeroshift_smallTTL.cfg");
        AerospikeClientProvider clientProvider = new AerospikeClientProvider(
                config);
        AerospikeSessionStore store = new AerospikeSessionStore(config,
                clientProvider.get(), null);
        store.put("small_session", "name", "Jane Doe");
        store.put("small_session", "roll_no", 21);
        try {
            Thread.sleep(10000);
            store.get("small_session", "name");
            Assert.fail("Record still found");
        } catch (SessionNotFound e) {
            System.out.println("Session expired!");
        }
    }

    /*
     * Test method for smallTTL. If Get() method is called, the TTL of the
     * record will be updated.
     */
    @Test
    public void smallTTLtestGetmethod() throws IOException,
            InterruptedException, SessionStoreException, SessionNotFound {
        ConfigReader configReader = new ConfigReader();
        AerospikeSessionStoreConfig config = configReader
                .getConfiguration("aeroshift_smallTTL.cfg");
        AerospikeClientProvider clientProvider = new AerospikeClientProvider(
                config);
        AerospikeSessionStore store = new AerospikeSessionStore(config,
                clientProvider.get(), null);
        store.put("peterParker", "alias", "spidey");
        @Cleanup
        AerospikeClient client = new AerospikeClient("127.0.0.1", 3000);
        Key key = new Key("test", "users", "peterParker");
        Record record1 = client.get(null, key);
        int oldExp = record1.expiration;
        Thread.sleep(5000);
        store.get("peterParker", "alias");
        Record record2 = client.get(null, key);
        int newExp = record2.expiration;
        Assert.assertTrue(newExp - oldExp >= 5);
    }

    /*
     * Test method for smallTTL: If GetAll() method is called to retrieve the
     * record, the TTL of the record will be updated
     */
    @Test
    public void smallTTLtestGetAllmethod() throws IOException,
            InterruptedException, SessionStoreException, SessionNotFound {
        ConfigReader configReader = new ConfigReader();
        AerospikeSessionStoreConfig config = configReader
                .getConfiguration("aeroshift_smallTTL.cfg");
        AerospikeClientProvider clientProvider = new AerospikeClientProvider(
                config);
        AerospikeSessionStore store = new AerospikeSessionStore(config,
                clientProvider.get(), null);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("home", "gotham");
        map.put("mentor", "Ra's AlGhul");
        map.put("BFF", "Joker");
        store.putAll("bruceWayne", map);
        @Cleanup
        AerospikeClient client = new AerospikeClient("127.0.0.1", 3000);
        Key key = new Key("test", "users", "bruceWayne");
        Record record1 = client.get(null, key);
        int oldExp = record1.expiration;
        Thread.sleep(5000);
        store.getAll("bruceWayne");
        Record record2 = client.get(null, key);
        int newExp = record2.expiration;
        Assert.assertTrue(newExp - oldExp >= 5);
    }

    /*
     * Test method for the case when the TTL never expires
     */
    @Test
    public void neverexpireTTLtest() throws SessionStoreException,
            SessionNotFound, IOException, InterruptedException {
        ConfigReader configReader = new ConfigReader();
        AerospikeSessionStoreConfig config = configReader
                .getConfiguration("aeroshift_neverexpireTTL.cfg");
        AerospikeClientProvider clientProvider = new AerospikeClientProvider(
                config);
        AerospikeSessionStore store = new AerospikeSessionStore(config,
                clientProvider.get(), null);
        store.put("nexpire_session", "name", "Joe Doe");
        store.put("nexpire_session", "roll_no", 21);
        Thread.sleep(10000);
        Assert.assertEquals("Joe Doe", store.get("nexpire_session", "name"));
    }
}
