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

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Host;
import com.aerospike.client.policy.ClientPolicy;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Provider for AerospikeClient
 *
 * @author akshay
 *
 */
@Singleton
@RequiredArgsConstructor(onConstructor = @_(@Inject))
public class AerospikeClientProvider implements Provider<AerospikeClient> {
    /**
     * The session store configuration.
     */
    private final AerospikeSessionStoreConfig config;

    /**
     * (non-Javadoc)
     *
     * @see com.google.inject.Provider#get()
     */
    @Override
    public AerospikeClient get() {

        ClientPolicy cpolicy = new ClientPolicy();
        return new AerospikeClient(cpolicy, config.getHosts().toArray(
                new Host[0]));
    }
}
