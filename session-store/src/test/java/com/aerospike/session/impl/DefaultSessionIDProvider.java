package com.aerospike.session.impl;

import java.util.Random;

import com.aerospike.session.SessionIDProvider;

public class DefaultSessionIDProvider implements SessionIDProvider {
    private String sessionid;

    public void setSessionID() {
        String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        int string_length = 10;
        StringBuffer random_str = new StringBuffer();
        for (int i = 0; i < string_length; i++) {
            int randomInt = 0;
            Random randomGenerator = new Random();
            randomInt = randomGenerator.nextInt(CHAR_LIST.length());
            random_str.append(CHAR_LIST.charAt(randomInt));
        }
    }

    @Override
    public String get() {
        if (sessionid == null) {
            setSessionID();
        }
        return sessionid;
    }
}
