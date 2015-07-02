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

import lombok.RequiredArgsConstructor;
import play.Configuration;
import play.Environment;

import com.aerospike.session.impl.AerospikeClientModule;
import com.aerospike.session.impl.AerospikeSessionStoreConfig;
import com.aerospike.session.impl.SessionStoreModule;
import com.aerospike.transcoder.TranscoderModule;
import com.aerospike.transcoder.classloader.TranscoderClassLoader;
import com.aerospike.transcoder.fst.FstconfigModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * MasterModule installs all the dependent modules
 *
 * @author akshay
 *
 */
@RequiredArgsConstructor
public class MasterModule extends AbstractModule {

    private final Environment environment;
    @SuppressWarnings("unused")
    private final Configuration configuration;

    @Singleton
    @Provides
    @TranscoderClassLoader
    ClassLoader getClassLoader() {
        return environment.classLoader();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        install(new SessionStoreModule());
        install(new AerospikeClientModule());
        install(new TranscoderModule());
        install(new FstconfigModule());
        bind(AerospikeSessionStoreConfig.class).toProvider(
                AerospikePlayConfigReader.class).in(Singleton.class);
    }

}
