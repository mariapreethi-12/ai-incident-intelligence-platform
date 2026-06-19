package com.mariapreethi.incidentintelligence.kafka;

import com.mariapreethi.incidentintelligence.config.AppProperties;
import com.mariapreethi.incidentintelligence.model.ServiceEvent;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaEventPublisher implements EventPublisher {
    private final KafkaTemplate<String, ServiceEvent> kafkaTemplate;
    private final AppProperties properties;

    public KafkaEventPublisher(KafkaTemplate<String, ServiceEvent> kafkaTemplate, AppProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    @Override
    public void publish(ServiceEvent event) {
        kafkaTemplate.send(properties.kafkaTopic(), event.getTraceId(), event);
    }
}
