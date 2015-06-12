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

import java.util.HashMap;
import java.util.Map;

import lombok.Cleanup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.session.SessionNotFound;
import com.aerospike.session.SessionStoreException;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author akshay
 *
 */
public class AerospikeSessionStoreTest {
    private Injector injector;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setup() throws Exception {
        injector = Guice.createInjector(new MasterModule());
    }

    /**
     * Test method for
     * {@link com.aerospike.session.impl.AerospikeSessionStore#put(java.lang.String, java.lang.String, java.lang.Object)}
     * .
     *
     * @throws SessionNotFound
     * @throws SessionStoreException
     */
    @Test
    public void testPut() throws SessionStoreException, SessionNotFound {
        AerospikeSessionStore store = injector
                .getInstance(AerospikeSessionStore.class);

        store.put("aero", "location", "bangalore");

        Assert.assertEquals("bangalore", store.get("aero", "location"));
    }

    @Test
    public void testPutObject() throws SessionStoreException, SessionNotFound {

        AerospikeSessionStore store = injector
                .getInstance(AerospikeSessionStore.class);
        Student student = new Student();
        student.name = "RocketRaccoon";
        student.rollno = "A1";
        student.subject = "Physics";
        store.put("RR", "student", student);

        Assert.assertEquals(student, store.get("RR", "student"));
    }

    /**
     * Test method for
     * {@link com.aerospike.session.impl.AerospikeSessionStore#putAll(java.lang.String, java.util.Map)}
     * .
     *
     * @throws SessionNotFound
     * @throws SessionStoreException
     */
    @Test
    public void testPutAll() throws SessionStoreException, SessionNotFound {
        AerospikeSessionStore store = injector
                .getInstance(AerospikeSessionStore.class);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("home", "gotham");
        map.put("mentor", "Ra's AlGhul");
        map.put("BFF", "Joker");
        store.putAll("bruceWayne", map);

        Assert.assertEquals("Joker", store.get("bruceWayne", "BFF"));
    }

    /**
     * Test method for
     * {@link com.aerospike.session.impl.AerospikeSessionStore#get(java.lang.String, java.lang.String)}
     * .
     *
     * @throws SessionNotFound
     * @throws SessionStoreException
     */
    @Test
    public void testGet() throws SessionStoreException, SessionNotFound {
        AerospikeSessionStore store = injector
                .getInstance(AerospikeSessionStore.class);
        store.put("Stark", "name", "Tony");
        Assert.assertEquals("Tony", store.get("Stark", "name"));
    }

    /**
     * Test method for
     * {@link com.aerospike.session.impl.AerospikeSessionStore#getAll(java.lang.String)}
     * .
     *
     * @throws SessionNotFound
     * @throws SessionStoreException
     */
    @Test
    public void testGetAll() throws SessionStoreException, SessionNotFound {
        AerospikeSessionStore store = injector
                .getInstance(AerospikeSessionStore.class);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("home", "gotham");
        map.put("mentor", "Ra's AlGhul");
        map.put("BFF", "Joker");
        store.putAll("bruceWayne", map);

        Assert.assertEquals("Joker", store.get("bruceWayne", "BFF"));
    }

    /**
     * Test method for
     * {@link com.aerospike.session.impl.AerospikeSessionStore#touch(java.lang.String)}
     * .
     *
     * @throws SessionNotFound
     * @throws SessionStoreException
     * @throws InterruptedException
     */
    @Test
    public void testTouch() throws SessionStoreException, SessionNotFound,
            InterruptedException {

        AerospikeSessionStore store = injector
                .getInstance(AerospikeSessionStore.class);
        store.put("defsession", "name", "jane doe");
        @Cleanup
        AerospikeClient client = new AerospikeClient("127.0.0.1", 3000);
        Key key = new Key("test", "users", "defsession");
        Record record1 = client.get(null, key);
        int oldExp = record1.expiration;
        Thread.sleep(5000);
        store.get("defsession", "name");
        Record record2 = client.get(null, key);
        int newExp = record2.expiration;
        Assert.assertTrue(newExp - oldExp >= 5);

    }

    /**
     * Test method for
     * {@link com.aerospike.session.impl.AerospikeSessionStore#destroy(java.lang.String)}
     * .
     *
     * @throws SessionNotFound
     * @throws SessionStoreException
     */
    @Test
    public void testDestroy() throws SessionStoreException, SessionNotFound {
        AerospikeSessionStore store = injector
                .getInstance(AerospikeSessionStore.class);
        store.put("mysession", "name", "john doe");
        Assert.assertEquals("john doe", store.get("mysession", "name"));
        store.destroy("mysession");

        try {
            store.get("mysession", "name");
            Assert.fail("Exception not thrown");
        } catch (SessionNotFound e) {
            System.out.println("The session successfully destroyed!!");
        }

    }

    /**
     * Test method for
     * {@link com.aerospike.session.impl.AerospikeSessionStore#exists(java.lang.String)}
     * .
     */
    @Test
    public void testExists() {
        AerospikeSessionStore store = injector
                .getInstance(AerospikeSessionStore.class);
        store.put("mathewMurdock", "alias", "dareDevil");
        boolean response = store.exists("mathewMurdock");
        Assert.assertTrue("The record exists", response);
    }

}
