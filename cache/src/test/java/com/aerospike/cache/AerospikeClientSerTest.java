package com.aerospike.cache;

import java.util.Arrays;
import java.util.List;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Log;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.GenerationPolicy;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;

/**
 * Hello world!
 *
 */
public class AerospikeClientSerTest {
    private AerospikeClient client;

    public AerospikeClientSerTest() {
        ClientPolicy cpolicy = new ClientPolicy();
        this.client = new AerospikeClient(cpolicy, "127.0.0.1", 3000);
        new WritePolicy();
    }

    public static void main(String[] args) throws AerospikeException {
        try {
            AerospikeClientSerTest as = new AerospikeClientSerTest();
            as.work();
        } catch (Exception e) {
            Log.error("Critical error");
        }
    }

    public void work() throws Exception {
        List<String> persons = Arrays.asList("Bill Gates", "Steve Jobs");
        Media media = new Media("http://javaone.com/keynote.mpg",
                "Javaone Keynote", 640, 480, "video/mpg4", 18000000, 58982400,
                262144, true, persons, Media.Player.JAVA, null);
        Image image1 = new Image("http://javaone.com/keynote_large.jpg",
                "Javaone Keynote", 1024, 768, Image.Size.LARGE);
        Image image2 = new Image("http://javaone.com/keynote_small.jpg",
                "Javaone Keynote", 320, 240, Image.Size.SMALL);
        List<Image> images = Arrays.asList(image1, image2);

        MediaContent mediaContent = new MediaContent(media, images);

        long putstartTimeMs = System.currentTimeMillis();
        System.out.println("startTimeput:" + putstartTimeMs);
        WritePolicy wPolicy = new WritePolicy();
        wPolicy.generationPolicy = GenerationPolicy.EXPECT_GEN_EQUAL;
        wPolicy.recordExistsAction = RecordExistsAction.REPLACE;

        for (int i = 1; i < 100001; i++) {
            Key key = new Key("test", "users", Integer.toString(i));
            Bin bin1 = new Bin("cachebin", mediaContent);
            client.put(wPolicy, key, bin1);
        }
        long putstopTimeMs = System.currentTimeMillis();
        System.out.println("stopTimeput:" + putstopTimeMs);

        long getstartTimeMs = System.currentTimeMillis();
        System.out.println(getstartTimeMs);
        for (int j = 1; j < 100001; j++) {
            Key key = new Key("test", "users", Integer.toString(j));
            this.client.get(null, key);
        }
        long getstopTimeMs = System.currentTimeMillis();
        System.out.println(getstopTimeMs);

        System.out.println("PUT operation Took "
                + (putstopTimeMs - putstartTimeMs)
                + " ms\nGET operation Took: "
                + (getstopTimeMs - getstartTimeMs) + " ms");
    }
}
