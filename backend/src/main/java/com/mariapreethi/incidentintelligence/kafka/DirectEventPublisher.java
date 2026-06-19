package com.mariapreethi.incidentintelligence.kafka;

import com.mariapreethi.incidentintelligence.model.ServiceEvent;
import com.mariapreethi.incidentintelligence.service.EventIngestionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.kafka-enabled", havingValue = "false")
public class DirectEventPublisher implements EventPublisher {
    private final EventIngestionService ingestionService;

    public DirectEventPublisher(EventIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Override
    public void publish(ServiceEvent event) {
        ingestionService.ingest(event);
    }
}
