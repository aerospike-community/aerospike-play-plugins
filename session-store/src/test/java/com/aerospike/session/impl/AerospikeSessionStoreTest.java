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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Cleanup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.session.SessionIDProvider;
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

        store.put("location", "bangalore");

        Assert.assertEquals("bangalore", store.get("location"));
    }

    /**
     * Test method for writing POJOS
     *
     * @throws SessionStoreException
     * @throws SessionNotFound
     */
    @Test
    public void testPutObject() throws SessionStoreException, SessionNotFound {

        AerospikeSessionStore store = injector
                .getInstance(AerospikeSessionStore.class);
        Student student = new Student();
        student.name = "RocketRaccoon";
        student.rollno = "A1";
        student.subject = "Physics";
        store.put("student", student);

        Assert.assertEquals(student, store.get("student"));
    }

    /**
     * Test method for reading POJOS
     *
     * @throws SessionStoreException
     * @throws SessionNotFound
     */
    @Test
    public void testPutList() throws SessionStoreException, SessionNotFound {
        AerospikeSessionStore store = injector
                .getInstance(AerospikeSessionStore.class);
        List<Student> studentlist = new ArrayList<Student>();
        studentlist.add(new Student("John doe", "QWERTY101", "TOC"));
        studentlist.add(new Student("Jane doe", "ALPHA101", "DSA"));
        List<String> stringlist = new ArrayList<String>();
        stringlist.add("alphabets");
        stringlist.add("numericals");
        store.put("studentlist", stringlist);
        store.put("studentlist", studentlist);
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
        store.putAll(map);

        Assert.assertEquals("Joker", store.get("BFF"));
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
        store.put("name", "Tony");
        Assert.assertEquals("Tony", store.get("name"));
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
        store.putAll(map);

        Assert.assertEquals("Joker", store.get("BFF"));
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
        SessionIDProvider sessionIDProvider = injector
                .getInstance(SessionIDProvider.class);
        store.put("name", "jane doe");
        @Cleanup
        AerospikeClient client = new AerospikeClient("127.0.0.1", 3000);
        Key key = new Key("test", "users", sessionIDProvider.get());
        Record record1 = client.get(null, key);
        int oldExp = record1.expiration;
        System.out.println("old expiry:" + oldExp);
        Thread.sleep(5000);
        store.get("name");
        Record record2 = client.get(null, key);
        int newExp = record2.expiration;
        System.out.println("new expiry:" + newExp);
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
        store.put("name", "john doe");
        Assert.assertEquals("john doe", store.get("name"));
        store.destroy();

        try {
            store.get("name");
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
        store.put("alias", "dareDevil");
        boolean response = store.exists();
        Assert.assertTrue("The record exists", response);
    }

    /**
     * Test method for List of POJO
     *
     * @throws SessionStoreException
     * @throws SessionNotFound
     */
    @Test
    public void testGetList() throws SessionStoreException, SessionNotFound {
        AerospikeSessionStore store = injector
                .getInstance(AerospikeSessionStore.class);
        List<Student> studentlist = new ArrayList<Student>();
        studentlist.add(new Student("John doe", "QWERTY101", "TOC"));
        studentlist.add(new Student("Jane doe", "ALPHA101", "DSA"));
        store.put("studentlist", studentlist);

        System.out.println(store.get("studentlist"));
        Assert.assertEquals(store.get("studentlist"), studentlist);
    }

    /**
     * Test method for Map consisting of POJO as Key/Value
     *
     * @throws SessionStoreException
     * @throws SessionNotFound
     */
    @Test
    public void testGetMap() throws SessionStoreException, SessionNotFound {
        AerospikeSessionStore store = injector
                .getInstance(AerospikeSessionStore.class);
        Map<GateKey, String> map = new HashMap<GateKey, String>();
        map.put(new GateKey(true, true), "TRUE");
        map.put(new GateKey(true, false), "FALSE");
        map.put(new GateKey(false, true), "FALSE");
        map.put(new GateKey(false, false), "FALSE");
        store.put("AND gate", map);
        System.out.println(store.get("AND gate"));
        Assert.assertEquals(store.get("AND gate"), map);
    }

    /**
     * Test method for List of Maps containing POJOS
     *
     * @throws SessionStoreException
     * @throws SessionNotFound
     */
    @Test
    public void testGetListofMap() throws SessionStoreException,
            SessionNotFound {
        AerospikeSessionStore store = injector
                .getInstance(AerospikeSessionStore.class);
        List<Map<GateKey, String>> boolist = new ArrayList<Map<GateKey, String>>();
        Map<GateKey, String> ANDgate = new HashMap<GateKey, String>();
        ANDgate.put(new GateKey(true, true), "TRUE");
        ANDgate.put(new GateKey(true, false), "FALSE");
        ANDgate.put(new GateKey(false, true), "FALSE");
        ANDgate.put(new GateKey(false, false), "FALSE");
        boolist.add(ANDgate);
        Map<GateKey, String> ORgate = new HashMap<GateKey, String>();
        ORgate.put(new GateKey(true, true), "TRUE");
        ORgate.put(new GateKey(true, false), "FALSE");
        ORgate.put(new GateKey(false, true), "FALSE");
        ORgate.put(new GateKey(false, false), "FALSE");
        boolist.add(ORgate);
        store.put("allgates", boolist);
        Assert.assertEquals(store.get("allgates"), boolist);
    }

    /**
     * Testing for Map containing List of POJOS
     *
     * @throws SessionStoreException
     * @throws SessionNotFound
     */
    @Test
    public void testGetMapofList() throws SessionStoreException,
            SessionNotFound {
        AerospikeSessionStore store = injector
                .getInstance(AerospikeSessionStore.class);
        List<Student> class1 = new ArrayList<Student>();
        class1.add(new Student("Akki", "10", "DM"));
        class1.add(new Student("Sid", "18", "LO"));
        List<Student> class2 = new ArrayList<Student>();
        class2.add(new Student("Aki", "1", "TOC"));
        Map<Course, List<Student>> school = new HashMap<Course, List<Student>>();
        school.put(new Course(3, "John Doe"), class1);
        school.put(new Course(1, "Jane Doe"), class2);
        store.put("classroom", school);
        Assert.assertEquals(store.get("classroom"), school);
    }

    @Test
    public void testCreate() {
        AerospikeSessionStore store = injector
                .getInstance(AerospikeSessionStore.class);
        try {
            store.get("exists");
            Assert.fail("Session record not created");
        } catch (SessionNotFound e) {
            System.out.println("The session not found. Creating new session");
            store.create();
            try {
                store.get("exists");
            } catch (SessionStoreException | SessionNotFound e1) {

            }
        }
    }
}
