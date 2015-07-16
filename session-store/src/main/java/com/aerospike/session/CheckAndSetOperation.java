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

package com.aerospike.session;

import java.util.Map;

/**
 * An operation interface for providing atomic Read and Write operation.
 *
 * @author akshay
 *
 */
public interface CheckAndSetOperation {
    /**
     * Invoked with values in session store for current session. The resulting
     * map of execute method will be replace current values in session-store.
     *
     * <p>
     * This method should be idempotent because this could be invoked multiple
     * times for the same operation.
     * </p>
     *
     * @param curValues
     * @return
     */
    Map<String, Object> execute(Map<String, Object> curValues);
}
