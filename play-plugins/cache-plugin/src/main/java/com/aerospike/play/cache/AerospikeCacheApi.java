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


import java.util.concurrent.Callable;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.aerospike.cache.AerospikeCache;

import play.cache.CacheApi;

/**
 * Implementation of Play framework CacheApi using Aerospike 
 * @author akshay
 *
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @_(@Inject))
public class AerospikeCacheApi implements CacheApi {

	private final AerospikeCache acache;

	@SuppressWarnings("unchecked")
	/**
	 * (non-Javadoc)
	 * @see play.cache.CacheApi#get(java.lang.String)
	 */
	@Override
	public <T> T get(String key) {
		return (T) acache.get(key);
	}

	/**
	 * (non-Javadoc)
	 * @see play.cache.CacheApi#getOrElse(java.lang.String, java.util.concurrent.Callable)
	 */
	@Override
	public <T> T getOrElse(String key, Callable<T> block) {
		return acache.getOrElse(key, block, -1);
	}

	/**
	 * (non-Javadoc)
	 * @see play.cache.CacheApi#getOrElse(java.lang.String, java.util.concurrent.Callable, int)
	 */
	@Override
	public <T> T getOrElse(String key, Callable<T> block, int expiration) {
		return acache.getOrElse(key, block, expiration);
	}

	/**
	 * (non-Javadoc)
	 * @see play.cache.CacheApi#remove(java.lang.String)
	 */
	@Override
	public void remove(String key) {
		acache.remove(key);

	}

	/**
	 * (non-Javadoc)
	 * @see play.cache.CacheApi#set(java.lang.String, java.lang.Object)
	 */
	@Override
	public void set(String key, Object Value) {
		log.debug("called");
		acache.set(key, Value, -1);

	}

	/**
	 * (non-Javadoc)
	 * @see play.cache.CacheApi#set(java.lang.String, java.lang.Object, int)
	 */
	@Override
	public void set(String key, Object Value, int expiration) {

		acache.set(key, Value, expiration);

	}

}
