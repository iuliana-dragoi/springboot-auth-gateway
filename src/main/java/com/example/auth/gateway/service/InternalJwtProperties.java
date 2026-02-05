package com.example.auth.gateway.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "internal.jwt")
@Getter
@Setter
public class InternalJwtProperties {

    private String secret;
    private long expiration;
}
