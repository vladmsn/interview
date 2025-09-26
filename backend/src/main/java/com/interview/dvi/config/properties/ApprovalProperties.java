package com.interview.dvi.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "dvi.approval")
public record ApprovalProperties (
        String secret,
        Duration ttl,
        String baseUrl
) {}
