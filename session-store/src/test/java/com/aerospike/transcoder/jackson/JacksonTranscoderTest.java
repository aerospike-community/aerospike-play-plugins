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

package com.aerospike.transcoder.jackson;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author akshay
 *
 */
public class JacksonTranscoderTest {

    /*
     * Test method for {@link
     * com.aerospike.transcoder.jackson.JacksonTranscoder#encode()} and {@link
     * com.aerospike.transcoder.jackson.JacksonTranscoder#decode()}
     */
    @Test
    public void test() {
        Student student = new Student();
        student.setName("Akshay");
        student.setRollno("cs12b1005");
        student.setSubject("asadsfa");
        JacksonTranscoder jackson = new JacksonTranscoder(new ObjectMapper());
        byte[] barray = jackson.encode(student);
        Assert.assertEquals(student, jackson.decode(barray));

    }

}
