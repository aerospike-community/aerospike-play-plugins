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

import java.util.Map;

import com.aerospike.session.CheckAndSetOperation;

/**
 * @author akshay
 *
 */
public class SessionOperatorTest implements CheckAndSetOperation {

    /**
     * (non-Javadoc)
     *
     * @see com.aerospike.session.impl.SessionOperation#execute(java.util.Map)
     */
    @Override
    public Map<String, Object> execute(Map<String, Object> curBin) {
        return null;
    }

}
