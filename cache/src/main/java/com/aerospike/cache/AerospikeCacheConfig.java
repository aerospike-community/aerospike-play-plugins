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

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import com.aerospike.client.Host;
import com.aerospike.transcoder.fst.FstTranscoder;

@Getter
@RequiredArgsConstructor
@ToString
public class AerospikeCacheConfig {
	private final List<Host> hosts;
	private final String username;
	private final String password;
	private final String namespace;
	private final String set;
	private final String bin;
	private final String transcoderFQCN;
	
	@SuppressWarnings("unused")
	private AerospikeCacheConfig(){
		this(null,null,null,null,null,null,FstTranscoder.class.getCanonicalName());
	}
	
}
