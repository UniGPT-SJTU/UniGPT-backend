package com.ise.unigpt.utils;

import java.util.Base64;
public class Base64Utils {
    public static String encode(String str1, String str2) {
        String combined = str1 + ":" + str2;
        String encoded = Base64.getEncoder().encodeToString(combined.getBytes());
        return encoded;
    }
}
