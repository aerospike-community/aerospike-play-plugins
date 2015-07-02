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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import lombok.RequiredArgsConstructor;
import play.Configuration;

import com.aerospike.client.Host;
import com.aerospike.session.impl.AerospikeSessionStoreConfig;

/**
 * Provider for AerospikeSessionStoreConfig class
 * 
 * @author akshay
 *
 */
@RequiredArgsConstructor(onConstructor = @_(@Inject))
public class AerospikePlayConfigReader implements
        Provider<AerospikeSessionStoreConfig> {
    private final Configuration configuration;

    /**
     * (non-Javadoc)
     * 
     * @see javax.inject.Provider#get()
     */
    @Override
    public AerospikeSessionStoreConfig get() {
        List<Map<String, Object>> hostMapList = configuration
                .getObjectList("play.sessionstore.aerospike.hosts");
        List<Host> hosts = new ArrayList<Host>();
        for (Map<String, Object> map : hostMapList) {
            hosts.add(new Host((String) map.get("name"), (Integer) map
                    .get("port")));
        }
        String username = configuration
                .getString("play.sessionstore.aerospike.username");
        String password = configuration
                .getString("play.sessionstore.aerospike.password");
        String namespace = configuration
                .getString("play.sessionstore.aerospike.namespace");
        String set = configuration.getString("play.sessionstore.aerospike.set");
        int sessionMaxAge = configuration
                .getInt("play.sessionstore.aerospike.sessionMaxAge");
        int checkAndSetMaxTries = configuration
                .getInt("play.sessionstore.aerospike.checkAndSetMaxTries");
        String sessionIdProviderFQCN = configuration
                .getString("play.sessionstore.aerospike.sessionIdProviderFQCN");
        String transcoderFQCN = configuration
                .getString("play.sessionstore.aerospike.transcoderFQCN");
        AerospikeSessionStoreConfig config = new AerospikeSessionStoreConfig(
                hosts, username, password, namespace, set, sessionMaxAge,
                transcoderFQCN, sessionIdProviderFQCN, checkAndSetMaxTries);
        return config;
    }
}
