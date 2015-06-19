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
import java.io.InputStream;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import com.aerospike.client.Host;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.Singleton;

/**
 * ConfigReader by default reads configuration supplied by the user
 * configuration file.
 *
 * @author akshay
 *
 */
@Slf4j
@Singleton
public class ConfigReader {
    /**
     * Read configuration from the filename provided by the user.
     */
    public AerospikeSessionStoreConfig getConfiguration(
            String configresourcename) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();

        log.debug("Reading store config from {}.", configresourcename);
        @Cleanup
        final InputStream stream = getClass().getClassLoader()
        .getResourceAsStream(configresourcename);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Host.class, new HostDeserializer());
        mapper.registerModule(module);
        System.out.println("\n\n" + mapper);
        return mapper.readValue(stream, AerospikeSessionStoreConfig.class);

    }

    /**
     * If filename is not supplied, read configuration from default file
     * 'aerospike-session-store.cfg'
     */
    public AerospikeSessionStoreConfig getConfiguration() throws IOException {
        return getConfiguration("aerospike-session-store.cfg");

    }

}
