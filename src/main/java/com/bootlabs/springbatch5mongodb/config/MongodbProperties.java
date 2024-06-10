package com.bootlabs.springbatch5mongodb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Properties specific to aws client.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb")
public class MongodbProperties {

    private String host;

    private String port;

    private String database;

    private String username;

    private String password;
}
