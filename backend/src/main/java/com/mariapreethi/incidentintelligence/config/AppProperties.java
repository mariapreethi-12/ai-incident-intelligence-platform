package com.mariapreethi.incidentintelligence.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String frontendUrl,
        boolean demoMode,
        boolean kafkaEnabled,
        String kafkaTopic,
        String openaiApiKey
) {
}
