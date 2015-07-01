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

import java.util.Map;

import com.aerospike.session.CheckAndSetOperation;

/**
 * A store for session data for Play Framework.
 *
 */
public interface AerospikePlaySessionStore {

    public void create();

    public void put(final String key, final Object value);

    public void putAll(final Map<String, Object> map);

    public Object get(final String key);

    public Map<String, Object> getAll();

    public void touch();

    public void destroy();

    public boolean exists();

    public void checkAndSet(CheckAndSetOperation operation);
}
