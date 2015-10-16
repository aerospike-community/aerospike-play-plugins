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

import java.util.Random;

import javax.inject.Singleton;

import com.aerospike.session.SessionIDProvider;

/**
 * Session Id provider for testing. Generates a random session id.
 *
 * @author ashish
 *
 */
@Singleton
public class DefaultSessionIDProvider implements SessionIDProvider {
    private String sessionid;

    public void setSessionID() {
        String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        int string_length = 10;
        StringBuffer random_str = new StringBuffer();
        for (int i = 0; i < string_length; i++) {
            int randomInt = 0;
            Random randomGenerator = new Random();
            randomInt = randomGenerator.nextInt(CHAR_LIST.length());
            random_str.append(CHAR_LIST.charAt(randomInt));
        }
        sessionid = random_str.toString();
    }

    @Override
    public String get() {
        if (sessionid == null) {
            setSessionID();
        }
        return sessionid;
    }
}
