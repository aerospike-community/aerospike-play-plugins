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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import lombok.extern.slf4j.Slf4j;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aerospike.cache.Image.Size;
import com.aerospike.cache.Media.Player;
import com.aerospike.transcoder.classloader.TranscoderSystemClassLoaderModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

@Slf4j
public class AerospikeCacheTest {
    private Injector injector;

    @Before
    public void setup() throws Exception {
        injector = Guice.createInjector(new MasterModule(),
                new TranscoderSystemClassLoaderModule());

    }

    @Test
    public void testSet() {
        AerospikeCache acache = injector.getInstance(AerospikeCacheImpl.class);
        log.debug("Testing set operation");
        acache.set("name", "Jane Doe", 10);
        Assert.assertEquals("Jane Doe", acache.get("name"));
    }

    @Test
    public void testDestroy() throws InterruptedException {
        AerospikeCache acache = injector.getInstance(AerospikeCacheImpl.class);
        log.debug("Setting up cache");
        acache.set("Daredevil", "Matthew Murdock", 100);
        Thread.sleep(5000);
        acache.remove("Daredevil");
        try {
            Assert.assertNull(acache.get("Daredevil"));
        } catch (Exception e) {
            System.out.println("Cached data removed successfully!!");
        }
    }

    @Test
    public void testGetOrElse() throws Exception {
        AerospikeCacheImpl acache = injector
                .getInstance(AerospikeCacheImpl.class);
        Callable<String> callable = new Callable<String>() {

            @Override
            public String call() throws Exception {
                return "bruceBanner";
            }

        };
        acache.getOrElse("Hulk", callable, 30);
    }

    @Test
    public void testPerformance() throws Exception {
        AerospikeCache cache = injector.getInstance(AerospikeCache.class);
        List<String> persons = Arrays.asList("Bill Gates", "Steve Jobs");
        Media media = new Media("http://javaone.com/keynote.mpg",
                "Javaone Keynote", 640, 480, "video/mpg4", 18000000, 58982400,
                262144, true, persons, Player.JAVA, null);
        Image image1 = new Image("http://javaone.com/keynote_large.jpg",
                "Javaone Keynote", 1024, 768, Size.LARGE);
        Image image2 = new Image("http://j avaone.com/keynote_small.jpg",
                "Javaone Keynote", 320, 240, Size.SMALL);
        List<Image> images = Arrays.asList(image1, image2);

        long putstartTimeMs = System.currentTimeMillis();

        MediaContent mediaContent = new MediaContent(media, images);

        for (int i = 1; i < 100001; i++) {
            cache.set(Integer.toString(i), mediaContent, -1);
        }
        long putstopTimeMs = System.currentTimeMillis();

        long getstartTimeMs = System.currentTimeMillis();
        for (int j = 1; j < 100001; j++) {
            cache.get(Integer.toString(j));
        }
        long getstopTimeMs = System.currentTimeMillis();
        System.out
        .println("PUT operation: " + (putstopTimeMs - putstartTimeMs));
        System.out
        .println("GET operation: " + (getstopTimeMs - getstartTimeMs));

    }
}
