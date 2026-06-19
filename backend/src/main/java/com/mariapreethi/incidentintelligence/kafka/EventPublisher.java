package com.mariapreethi.incidentintelligence.kafka;

import com.mariapreethi.incidentintelligence.model.ServiceEvent;

public interface EventPublisher {
    void publish(ServiceEvent event);
}
