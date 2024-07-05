package com.ise.unigpt.utils;

public class SetProxy {
    public static void setProxy() {
        String proxyPort = "7890";  // 代理端口
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", proxyPort);
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", proxyPort);
    }
}
