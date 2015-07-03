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

package com.aerospike.transcoder.fst;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.aerospike.transcoder.TranscodeException;
import com.aerospike.transcoder.classloader.TranscoderSystemClassLoaderModule;
import com.aerospike.transcoder.jackson.Student;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author akshay
 *
 */
public class FstTranscoderTest {

    /**
     * Test method for
     * {@link com.aerospike.transcoder.fst.FstTranscoder#encode()} and
     * {@link com.aerospike.transcoder.fst.FstTranscoder#decode()}
     */
    @Test
    public void test() throws TranscodeException, IOException {
        Student student = new Student();
        student.setName("Akshay");
        student.setRollno("cs12b1005");
        student.setSubject("Computer SCience");
        // FstTranscoder fst = new FstTranscoder();
        Injector injector = Guice.createInjector(new FstconfigModule(),
                new TranscoderSystemClassLoaderModule());
        FstTranscoder fst = injector.getInstance(FstTranscoder.class);
        byte[] barray;

        barray = fst.encode(student);
        fst.decode(barray);
        System.out.println(fst.decode(barray));
        Assert.assertEquals(student, fst.decode(barray));

    }

}
