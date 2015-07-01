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
package com.aerospike.play.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;
import com.aerospike.cache.AerospikeCacheConfig;
import com.aerospike.client.Host;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import play.Configuration;

/**
 * Read configuration from file src/main/resources/reference.conf
 * @author akshay
 *
 */
@RequiredArgsConstructor(onConstructor = @_(@Inject))
@Slf4j
public class PlayConfigReader implements Provider<AerospikeCacheConfig>{
	private final Configuration configuration; 
	
	/**
	 * (non-Javadoc)
	 * @see javax.inject.Provider#get()
	 */
	public AerospikeCacheConfig get(){
		
		List<Map<String, Object>> hostMaplist = configuration.getObjectList("play.cache.aerospike.hosts");
		List<Host> hosts = new ArrayList<Host>();
		for (Map<String,Object> map : hostMaplist) {
			hosts.add(new Host((String)map.get("name"), (Integer) map.get("port")));
		}
		
		String username = configuration.getString("play.cache.aerospike.username");
		String password = configuration.getString("play.cache.aerospike.password");
		String namespace = configuration.getString("play.cache.aerospike.namespace");
		String set = configuration.getString("play.cache.aerospike.set");
		String bin = configuration.getString("play.cache.aerospike.bin");
		String transcoderFQCN = configuration.getString("play.cache.aerospike.transcoderFQCN");
		
	
		AerospikeCacheConfig config = new AerospikeCacheConfig(hosts,username,password,namespace,set,bin,transcoderFQCN);
		log.debug("Aerospike config: {}",config);
		return config;
	}

}
