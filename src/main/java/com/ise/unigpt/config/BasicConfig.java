package com.ise.unigpt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BasicConfig {
    @Value("${imageServerUrl}")
    public final String IMAGE_SERVER_URL;

    public BasicConfig(@Value("${imageServerUrl}") String imageServerUrl) {
        IMAGE_SERVER_URL = imageServerUrl;
    }
}
