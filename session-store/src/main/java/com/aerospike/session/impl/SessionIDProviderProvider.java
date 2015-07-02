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

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.aerospike.session.SessionIDProvider;
import com.aerospike.transcoder.classloader.TranscoderClassLoader;
import com.google.inject.Injector;

/**
 * @author akshay
 *
 */
@Singleton
public class SessionIDProviderProvider implements Provider<SessionIDProvider> {

    private final AerospikeSessionStoreConfig config;
    private final Injector injector;
    private final ClassLoader classLoader;

    @Inject
    public SessionIDProviderProvider(AerospikeSessionStoreConfig config,
            Injector injector, @TranscoderClassLoader ClassLoader classLoader) {
        this.config = config;
        this.injector = injector;
        this.classLoader = classLoader;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.inject.Provider#get()
     */
    @SuppressWarnings("unchecked")
    @Override
    public SessionIDProvider get() {
        String classname = config.getSessionIdProviderFQCN();
        Class<SessionIDProvider> sessionIDProviderClass;
        try {
            sessionIDProviderClass = (Class<SessionIDProvider>) classLoader
                    .loadClass(classname);
            return injector.getInstance(sessionIDProviderClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unknown SessionIDProvider class "
                    + config.getSessionIdProviderFQCN(), e);
        }
    }

}
