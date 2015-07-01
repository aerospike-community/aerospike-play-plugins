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

import java.util.concurrent.Callable;

import lombok.extern.slf4j.Slf4j;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

@Slf4j
public class AerospikeCacheTest {
	private Injector injector;
	@Before
	public void setup() throws Exception{
		injector = Guice.createInjector(new MasterModule());
		
	}
	
	@Test
	public void testSet() {
		AerospikeCache acache = injector.getInstance(AerospikeCache.class);
		log.debug("Testing set operation");
		acache.set("name", "Jane Doe", 10);
		Assert.assertEquals("Jane Doe", acache.get("name"));
	}

	@Test
	public void testDestroy() throws InterruptedException{
		AerospikeCache acache = injector.getInstance(AerospikeCache.class);
		log.debug("Setting up cache");
		acache.set("Daredevil","Matthew Murdock",100);
		Thread.sleep(5000);
		acache.remove("Daredevil");
		try {
            acache.get("Daredevil");
            Assert.fail("Exception not thrown");
        } catch (Exception e) {
            System.out.println("Cached data removed successfully!!");
        }
	}
	
	@Test
	public void testGetOrElse(){
		AerospikeCache acache = injector.getInstance(AerospikeCache.class);
		Callable<String> callable = new Callable<String>(){

			@Override
			public String call() throws Exception {
				return "bruceBanner";
			}
			
		};
		acache.getOrElse("Hulk", callable, 30);
	}
}
