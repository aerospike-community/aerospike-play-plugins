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

package com.aerospike.transcoder;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.aerospike.session.impl.AerospikeSessionStoreConfig;
import com.aerospike.session.impl.ConfigReader;
import com.aerospike.session.impl.MasterModule;
import com.aerospike.transcoder.fst.FstTranscoder;
import com.aerospike.transcoder.jackson.JacksonTranscoder;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author akshay
 *
 */
public class TranscoderModuleTest {

    /**
     * Test method for {@link com.aerospike.transcoder.TranscoderProvider#get()}
     */
    @Test
    public void testDefault() {
        Injector injector = Guice.createInjector(new MasterModule());
        AerospikeSessionStoreConfig config = injector
                .getInstance(AerospikeSessionStoreConfig.class);
        TranscoderProvider transcoderProvider = new TranscoderProvider(config,
                injector, getClass().getClassLoader());
        transcoderProvider.get();
        Assert.assertTrue(transcoderProvider.get() instanceof FstTranscoder);
    }

    /**
     * Test method for {@link com.aerospike.transcoder.TranscoderProvider#get()}
     */
    @Test
    public void testJackson() throws IOException {
        Injector injector = Guice.createInjector(new MasterModule());
        ConfigReader configReader = new ConfigReader();
        AerospikeSessionStoreConfig config = configReader
                .getConfiguration("aeroshift_jackson.cfg");
        TranscoderProvider transcoderProvider = new TranscoderProvider(config,
                injector, getClass().getClassLoader());
        transcoderProvider.get();
        Assert.assertTrue(transcoderProvider.get() instanceof JacksonTranscoder);
    }
}
