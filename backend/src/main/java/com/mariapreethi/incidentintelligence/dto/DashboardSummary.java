package com.mariapreethi.incidentintelligence.dto;

import java.util.Map;

public record DashboardSummary(
        long totalEvents,
        long openIncidents,
        long criticalIncidents,
        double averageLatencyMs,
        Map<String, Long> eventsByService,
        Map<String, Long> incidentsBySeverity
) {
}
