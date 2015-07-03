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

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import com.aerospike.client.Host;
import com.aerospike.transcoder.fst.FstTranscoder;

/**
 * Configuration for the aerospike session store.
 *
 * @author ashish
 *
 */
@Getter
@RequiredArgsConstructor
@ToString
public class AerospikeSessionStoreConfig {

    /**
     * The aerospike endpoints / nodes for the cluster.
     */
    private final List<Host> hosts;

    /**
     * The aerospike username.
     */
    private final String username;

    /**
     * The aerospike password.
     */
    private final String password;

    /**
     * The aerospike namespace to use.
     */
    private final String namespace;

    /**
     * The aerospike set storing session data.
     */
    private final String set;

    /**
     * The session timeout / maximum age. A session expires if time equal to max
     * age has elapsed since the last get/ put / touch operation on this
     * session.
     * <p>
     * Expiration values:
     * <ul>
     * <li>-1: Never expire for Aerospike 2 server versions >= 2.7.2 and
     * Aerospike 3 server versions >= 3.1.4. Do not use -1 for older servers.</li>
     * <li>0: Default to namespace configuration variable "default-ttl" on the
     * server.</li>
     * <li>greater than 0: Actual expiration in seconds.<br>
     * </li>
     * </ul>
     * </p>
     */

    private final int sessionMaxAge;

    /**
     * The fully qualified transcoder class name.
     */
    private final String transcoderFQCN;
    /**
     * The fully qualified session id provider class name.
     */
    private final String sessionIdProviderFQCN;

    /**
     * Private constructor required by a lot of serializers. e.g. Jackson,
     * Hibernate, etc.
     */

    private final int checkAndSetMaxTries;

    @SuppressWarnings("unused")
    private AerospikeSessionStoreConfig() {
        this(null, null, null, null, null, 0, FstTranscoder.class
                .getCanonicalName(), null, 4);
    }
}
