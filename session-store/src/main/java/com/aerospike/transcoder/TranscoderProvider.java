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

package com.aerospike.transcoder;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import lombok.RequiredArgsConstructor;

import com.aerospike.session.impl.AerospikeSessionStoreConfig;
import com.google.inject.Injector;

/**
 * @author akshay
 *
 */
@Singleton
@RequiredArgsConstructor(onConstructor = @_(@Inject))
public class TranscoderProvider implements Provider<Transcoder> {
    private final AerospikeSessionStoreConfig config;
    private final Injector injector;

    @SuppressWarnings("unchecked")
    /*
     * (non-Javadoc)
     * 
     * @see javax.inject.Provider#get()
     */
    @Override
    public Transcoder get() {
        String classname = config.getTranscoderFQCN();
        Class<Transcoder> transcoderClass;
        try {
            transcoderClass = (Class<Transcoder>) Class.forName(classname);
            return injector.getInstance(transcoderClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unknown transcode class "
                    + config.getTranscoderFQCN(), e);
        }

    }
}
