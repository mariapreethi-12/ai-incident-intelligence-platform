package com.mariapreethi.incidentintelligence.kafka;

import com.mariapreethi.incidentintelligence.config.AppProperties;
import com.mariapreethi.incidentintelligence.model.ServiceEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnProperty(name = "app.kafka-enabled", havingValue = "true")
public class KafkaProducerConfig {
    @Bean
    EventPublisher kafkaEventPublisher(KafkaTemplate<String, ServiceEvent> kafkaTemplate, AppProperties properties) {
        return new KafkaEventPublisher(kafkaTemplate, properties);
    }
}
