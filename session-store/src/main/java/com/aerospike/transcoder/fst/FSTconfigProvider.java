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

package com.aerospike.transcoder.fst;

import javax.inject.Provider;
import javax.inject.Singleton;

import org.nustaq.serialization.FSTConfiguration;

/**
 * Provider for FSTConfiguration
 *
 * @author akshay
 *
 */
@Singleton
public class FSTconfigProvider implements Provider<FSTConfiguration> {
    /*
     * (non-Javadoc)
     *
     * @see javax.inject.Provider#get()
     */
    @Override
    public FSTConfiguration get() {
        FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
        conf.setClassLoader(getClass().getClassLoader());
        return conf;
    }
}
