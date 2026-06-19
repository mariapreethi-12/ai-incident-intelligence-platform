package com.mariapreethi.incidentintelligence.service;

import com.mariapreethi.incidentintelligence.model.*;
import com.mariapreethi.incidentintelligence.repository.IncidentRepository;
import com.mariapreethi.incidentintelligence.repository.ServiceEventRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class IncidentDetectionService {
    private final IncidentRepository incidentRepository;
    private final ServiceEventRepository eventRepository;

    public IncidentDetectionService(IncidentRepository incidentRepository, ServiceEventRepository eventRepository) {
        this.incidentRepository = incidentRepository;
        this.eventRepository = eventRepository;
    }

    public void evaluate(ServiceEvent event) {
        Instant dedupeWindow = Instant.now().minus(10, ChronoUnit.MINUTES);
        if (event.getStatusCode() != null && event.getStatusCode() >= 500) {
            maybeCreate("5XX_BURST", event, Severity.ERROR, "5xx burst detected in " + event.getServiceName());
        }
        if (event.getLatencyMs() != null && event.getLatencyMs() > 1200) {
            maybeCreate("LATENCY_SPIKE", event, Severity.WARN, "Latency spike detected in " + event.getServiceName());
        }
        if ("payment-service".equals(event.getServiceName()) && event.getStatusCode() != null && event.getStatusCode() >= 500) {
            maybeCreate("PAYMENT_FAILURE", event, Severity.CRITICAL, "Payment failures affecting checkout");
        }
        if ("database-service".equals(event.getServiceName()) && event.getEventType() == EventType.TIMEOUT) {
            maybeCreate("DATABASE_TIMEOUT", event, Severity.CRITICAL, "Database timeout affecting dependent services");
        }
        if (eventRepository.countByStatusCodeGreaterThanEqualAndTimestampAfter(500, dedupeWindow) >= 5) {
            maybeCreate("HIGH_ERROR_RATE", event, Severity.CRITICAL, "High platform error rate detected");
        }
    }

    private void maybeCreate(String type, ServiceEvent event, Severity severity, String title) {
        Instant dedupeWindow = Instant.now().minus(10, ChronoUnit.MINUTES);
        if (incidentRepository.existsByIncidentTypeAndPrimaryServiceAndCreatedAtAfter(type, event.getServiceName(), dedupeWindow)) {
            return;
        }
        List<ServiceEvent> recent = eventRepository.findByTimestampAfterOrderByTimestampDesc(Instant.now().minus(5, ChronoUnit.MINUTES));
        String services = recent.stream().map(ServiceEvent::getServiceName).distinct().limit(5).collect(Collectors.joining(", "));
        Incident incident = new Incident();
        incident.setTitle(title);
        incident.setSummary(event.getMessage() + " Trace " + event.getTraceId() + " shows status " + event.getStatusCode() + " and latency " + event.getLatencyMs() + "ms.");
        incident.setSeverity(severity);
        incident.setStatus(IncidentStatus.OPEN);
        incident.setIncidentType(type);
        incident.setPrimaryService(event.getServiceName());
        incident.setAffectedServices(services.isBlank() ? event.getServiceName() : services);
        incident.setCreatedAt(Instant.now());
        incident.setUpdatedAt(Instant.now());
        incidentRepository.save(incident);
    }
}
