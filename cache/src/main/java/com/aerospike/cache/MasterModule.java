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

package com.aerospike.cache;

import com.aerospike.transcoder.TranscoderModule;
import com.aerospike.transcoder.fst.FstconfigModule;
import com.google.inject.AbstractModule;

/**
 * MasterModule installs all the dependent modules
 *
 * @author akshay
 *
 */
public class MasterModule extends AbstractModule {

    /**
     * (non-Javadoc)
     *
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        install(new AerospikeClientModule());
        install(new CacheConfigModule());
        install(new TranscoderModule());
        install(new FstconfigModule());
    }

}
