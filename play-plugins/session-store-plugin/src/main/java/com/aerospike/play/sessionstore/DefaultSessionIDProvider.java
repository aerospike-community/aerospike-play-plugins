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

import java.util.Random;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;

import play.mvc.Http;

import com.aerospike.session.SessionIDProvider;

/**
 * Default Provider for SessionID
 *
 * @author akshay
 *
 */
@Slf4j
public class DefaultSessionIDProvider implements SessionIDProvider {
    /*
     * (non-Javadoc)
     *
     * @see javax.inject.Provider#get()
     */
    @Override
    public String get() {
        play.mvc.Http.Context ctx = Http.Context.current();
        String sessionid = ctx.session().get("sessionid");
        if (StringUtils.isBlank(sessionid)) {
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
            ctx.session().put("sessionid", sessionid);
            log.debug("Setting up cookie");
        }
        return sessionid;
    }

}
