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

package com.aerospike.transcoder.classloader;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Binding module for classloader used in transcoder.
 *
 * @author akshay
 *
 */
public class TranscoderSystemClassLoaderModule extends AbstractModule {

    /*
     * (non-Javadoc)
     * 
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
    }

    @TranscoderClassLoader
    @Provides
    ClassLoader getClassLoader() {
        return getClass().getClassLoader();
    }

}
