package com.mariapreethi.incidentintelligence.kafka;

import com.mariapreethi.incidentintelligence.model.ServiceEvent;
import com.mariapreethi.incidentintelligence.service.EventIngestionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.kafka-enabled", havingValue = "true")
public class ServiceEventConsumer {
    private final EventIngestionService ingestionService;

    public ServiceEventConsumer(EventIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @KafkaListener(topics = "${app.kafka-topic}", groupId = "incident-intelligence")
    public void consume(ServiceEvent event) {
        ingestionService.ingest(event);
    }
}
