package com.ise.unigpt.config;

import org.slf4j.Logger;
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

    public final String POSTGRES_HOST;
    public final Integer POSTGRES_PORT;
    public final String POSTGRES_DB;
    public final String POSTGRES_USERNAME;
    public final String POSTGRES_PASSWORD;


    private final Logger log = org.slf4j.LoggerFactory.getLogger(BasicConfig.class);

    public BasicConfig(
            @Value("${imageServerUrl}") String imageServerUrl,
            @Value("${frontendServerUrl}") String frontendServerUrl,
            @Value("${jaccountClientId}") String jaccountClientId,
            @Value("${jaccountClientSecret}") String jaccountClientSecret,

            @Value("${postgres.host}") String postgresHost,
            @Value("${postgres.port}") Integer postgresPort,
            @Value("${postgres.database}") String postgresDatabase,
            @Value("${postgres.username}") String postgresUsername,
            @Value("${postgres.password}") String postgresPassword
        ) {

        IMAGE_SERVER_URL = imageServerUrl;
        FRONTEND_SERVER_URL = frontendServerUrl;
        JACCOUNT_CLIENT_ID = jaccountClientId;
        JACCOUNT_CLIENT_SECRET = jaccountClientSecret;
        ENCODED_JACCOUNT_CLIENT_ID_SECRET_COMBINED = Base64Utils.encode(jaccountClientId, jaccountClientSecret);
        
        this.POSTGRES_HOST = postgresHost;
        this.POSTGRES_PORT = postgresPort;
        this.POSTGRES_DB = postgresDatabase;
        this.POSTGRES_USERNAME = postgresUsername;
        this.POSTGRES_PASSWORD = postgresPassword;


        log.info("Basic Config");
        log.info("Image Server URL: " + IMAGE_SERVER_URL);
        log.info("Frontend Server URL: " + FRONTEND_SERVER_URL);
        log.info("JAccount Client ID: " + JACCOUNT_CLIENT_ID);
        log.info("JAccount Client Secret: " + JACCOUNT_CLIENT_SECRET);

        log.info("Postgres Host: " + POSTGRES_HOST);
        log.info("Postgres Port: " + POSTGRES_PORT);
        log.info("Postgres DB: " + POSTGRES_DB);
        log.info("Postgres Username: " + POSTGRES_USERNAME);
        log.info("Postgres Password: " + POSTGRES_PASSWORD);
        log.info("Basic Config Done");

    }
}
