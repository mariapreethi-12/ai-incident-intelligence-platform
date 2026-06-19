package com.mariapreethi.incidentintelligence.service;

import com.mariapreethi.incidentintelligence.dto.DashboardSummary;
import com.mariapreethi.incidentintelligence.model.*;
import com.mariapreethi.incidentintelligence.repository.IncidentRepository;
import com.mariapreethi.incidentintelligence.repository.ServiceEventRepository;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private final ServiceEventRepository eventRepository;
    private final IncidentRepository incidentRepository;

    public DashboardService(ServiceEventRepository eventRepository, IncidentRepository incidentRepository) {
        this.eventRepository = eventRepository;
        this.incidentRepository = incidentRepository;
    }

    public DashboardSummary summary() {
        var events = eventRepository.findTop75ByOrderByTimestampDesc();
        double avgLatency = events.stream().map(ServiceEvent::getLatencyMs).filter(v -> v != null).mapToInt(Integer::intValue).average().orElse(0);
        Map<String, Long> eventsByService = events.stream().collect(Collectors.groupingBy(ServiceEvent::getServiceName, Collectors.counting()));
        Map<String, Long> incidentsBySeverity = Arrays.stream(Severity.values()).collect(Collectors.toMap(Enum::name, incidentRepository::countBySeverity));
        return new DashboardSummary(
                eventRepository.count(),
                incidentRepository.countByStatus(IncidentStatus.OPEN),
                incidentRepository.countBySeverity(Severity.CRITICAL),
                Math.round(avgLatency),
                eventsByService,
                incidentsBySeverity
        );
    }
}
