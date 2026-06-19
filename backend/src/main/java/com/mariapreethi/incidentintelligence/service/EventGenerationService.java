package com.mariapreethi.incidentintelligence.service;

import com.mariapreethi.incidentintelligence.kafka.EventPublisher;
import com.mariapreethi.incidentintelligence.model.ServiceEvent;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EventGenerationService {
    private final EventFactory eventFactory;
    private final EventPublisher eventPublisher;

    public EventGenerationService(EventFactory eventFactory, EventPublisher eventPublisher) {
        this.eventFactory = eventFactory;
        this.eventPublisher = eventPublisher;
    }

    public List<ServiceEvent> generate(String scenario, int count) {
        List<ServiceEvent> events = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ServiceEvent event = eventFactory.randomEvent(scenario);
            eventPublisher.publish(event);
            events.add(event);
        }
        return events;
    }
}
