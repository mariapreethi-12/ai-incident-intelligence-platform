package com.mariapreethi.incidentintelligence.service;

import com.mariapreethi.incidentintelligence.model.ServiceEvent;
import com.mariapreethi.incidentintelligence.repository.ServiceEventRepository;
import org.springframework.stereotype.Service;

@Service
public class EventIngestionService {
    private final ServiceEventRepository eventRepository;
    private final IncidentDetectionService detectionService;

    public EventIngestionService(ServiceEventRepository eventRepository, IncidentDetectionService detectionService) {
        this.eventRepository = eventRepository;
        this.detectionService = detectionService;
    }

    public ServiceEvent ingest(ServiceEvent event) {
        ServiceEvent saved = eventRepository.save(event);
        detectionService.evaluate(saved);
        return saved;
    }
}
