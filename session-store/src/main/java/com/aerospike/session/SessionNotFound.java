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

package com.aerospike.session;

import lombok.Getter;

/**
 * Exception thrown when a session with given identifier is not found in the
 * store. This could happen if session never existed or expired.
 *
 * @author ashish
 *
 */
@Getter
public class SessionNotFound extends Exception {
    /**
     * The serial version id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The session id.
     */
    private final String sessionId;

    /**
     * Create a session not found exception.
     * 
     * @param sessionId
     */
    public SessionNotFound(final String sessionId) {
        super(String.format("Session %s not found.", sessionId));
        this.sessionId = sessionId;
    }
}
