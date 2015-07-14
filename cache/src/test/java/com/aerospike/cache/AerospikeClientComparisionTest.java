package com.aerospike.cache;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import lombok.Cleanup;

import org.junit.Before;
import org.junit.Test;

import com.aerospike.cache.Image.Size;
import com.aerospike.cache.Media.Player;
import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Host;
import com.aerospike.client.Key;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.GenerationPolicy;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.transcoder.classloader.TranscoderSystemClassLoaderModule;
import com.aerospike.transcoder.jackson.JacksonTranscoder;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * A test to compare cache get and put times with vanilla client.
 *
 */
public class AerospikeClientComparisionTest {
    /**
     *
     */
    private static final int NUM_KEYS = 100001;
    private Injector injector;

    @Before
    public void setup() throws Exception {
        injector = Guice.createInjector(new MasterModule(),
                new TranscoderSystemClassLoaderModule());

    }

    @Test
    public void testVanillaClient() throws Exception {
        ClientPolicy cpolicy = new ClientPolicy();
        @Cleanup
        AerospikeClient client = new AerospikeClient(cpolicy, "127.0.0.1", 3000);

        MediaContent mediaContent = getTestObject();

        long putstartTimeMs = System.currentTimeMillis();
        System.out.println("startTimeput:" + putstartTimeMs);
        WritePolicy wPolicy = new WritePolicy();
        wPolicy.generationPolicy = GenerationPolicy.EXPECT_GEN_EQUAL;
        wPolicy.recordExistsAction = RecordExistsAction.REPLACE;

        for (int i = 1; i < NUM_KEYS; i++) {
            Key key = new Key("test", "users", Integer.toString(i));
            Bin bin1 = new Bin("cachebin", mediaContent);
            client.put(wPolicy, key, bin1);
        }
        long putstopTimeMs = System.currentTimeMillis();
        System.out.println("stopTimeput:" + putstopTimeMs);

        long getstartTimeMs = System.currentTimeMillis();
        System.out.println(getstartTimeMs);
        for (int j = 1; j < NUM_KEYS; j++) {
            Key key = new Key("test", "users", Integer.toString(j));
            client.get(null, key);
        }
        long getstopTimeMs = System.currentTimeMillis();
        System.out.println(getstopTimeMs);

        System.out.println("PUT operation Took "
                + (putstopTimeMs - putstartTimeMs)
                + " ms\nGET operation Took: "
                + (getstopTimeMs - getstartTimeMs) + " ms");
    }

    @Test
    public void testCacheWithFst() throws Exception {
        AerospikeCache cache = injector.getInstance(AerospikeCache.class);
        MediaContent mediaContent = getTestObject();

        long putstartTimeMs = System.currentTimeMillis();
        for (int i = 1; i < NUM_KEYS; i++) {
            cache.set(Integer.toString(i), mediaContent, -1);
        }
        long putstopTimeMs = System.currentTimeMillis();

        long getstartTimeMs = System.currentTimeMillis();
        for (int j = 1; j < NUM_KEYS; j++) {
            cache.get(Integer.toString(j));
        }
        long getstopTimeMs = System.currentTimeMillis();
        System.out
                .println("PUT operation: " + (putstopTimeMs - putstartTimeMs));
        System.out
                .println("GET operation: " + (getstopTimeMs - getstartTimeMs));

    }

    @Test
    public void testCacheWithJackson() throws IOException,
            ClassNotFoundException {

        ConfigReader configreader = new ConfigReader();
        AerospikeCacheConfig config = configreader
                .getConfiguration("aerospike-jacksoncache.cfg");
        ClientPolicy cpolicy = new ClientPolicy();

        AerospikeCacheImpl cache = new AerospikeCacheImpl(config,
                new AerospikeClient(cpolicy, config.getHosts().toArray(
                        new Host[0])),
                        injector.getInstance(JacksonTranscoder.class));
        MediaContent mediaContent = getTestObject();
        long putstartTimeMs = System.currentTimeMillis();
        for (int i = 1; i < NUM_KEYS; i++) {
            cache.set(Integer.toString(i), mediaContent, -1);
        }
        long putstopTimeMs = System.currentTimeMillis();

        long getstartTimeMs = System.currentTimeMillis();
        for (int j = 1; j < NUM_KEYS; j++) {
            cache.get(Integer.toString(j));
        }
        long getstopTimeMs = System.currentTimeMillis();
        System.out
                .println("PUT operation: " + (putstopTimeMs - putstartTimeMs));
        System.out
                .println("GET operation: " + (getstopTimeMs - getstartTimeMs));
    }

    /**
     * @return the object used for testing.
     */
    private MediaContent getTestObject() {
        List<String> persons = Arrays.asList("Bill Gates", "Steve Jobs");
        Media media = new Media("http://javaone.com/keynote.mpg",
                "Javaone Keynote", 640, 480, "video/mpg4", 18000000, 58982400,
                262144, true, persons, Player.JAVA, null);
        Image image1 = new Image("http://javaone.com/keynote_large.jpg",
                "Javaone Keynote", 1024, 768, Size.LARGE);
        Image image2 = new Image("http://j avaone.com/keynote_small.jpg",
                "Javaone Keynote", 320, 240, Size.SMALL);
        List<Image> images = Arrays.asList(image1, image2);

        MediaContent mediaContent = new MediaContent(media, images);
        return mediaContent;
    }
}
