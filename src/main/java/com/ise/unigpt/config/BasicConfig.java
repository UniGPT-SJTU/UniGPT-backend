package com.ise.unigpt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ise.unigpt.utils.Base64Utils;

@Component
public class BasicConfig {
    public final String IMAGE_SERVER_URL;
    public final String FRONTEND_SERVER_URL;
    public final String JACCOUNT_CLIENT_ID;
    public final String JACCOUNT_CLIENT_SECRET;

    public final String ENCODED_JACCOUNT_CLIENT_ID_SECRET_COMBINED;

    public BasicConfig(
            @Value("${imageServerUrl}") String imageServerUrl,
            @Value("${frontendServerUrl}") String frontendServerUrl,
            @Value("${jaccountClientId}") String jaccountClientId,
            @Value("${jaccountClientSecret}") String jaccountClientSecret
        ) {

        IMAGE_SERVER_URL = imageServerUrl;
        FRONTEND_SERVER_URL = frontendServerUrl;
        JACCOUNT_CLIENT_ID = jaccountClientId;
        JACCOUNT_CLIENT_SECRET = jaccountClientSecret;
        ENCODED_JACCOUNT_CLIENT_ID_SECRET_COMBINED = Base64Utils.encode(jaccountClientId, jaccountClientSecret);

    }
}
