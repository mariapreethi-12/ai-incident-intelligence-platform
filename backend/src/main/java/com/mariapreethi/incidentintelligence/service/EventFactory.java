package com.mariapreethi.incidentintelligence.service;

import com.mariapreethi.incidentintelligence.model.*;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class EventFactory {
    private static final List<String> SERVICES = List.of("payment-service", "order-service", "auth-service", "inventory-service", "database-service");
    private final Random random = new Random();

    public ServiceEvent randomEvent(String scenario) {
        String requested = scenario == null ? "mixed" : scenario.toLowerCase();
        return switch (requested) {
            case "payment-failure" -> event("payment-service", EventType.ERROR, Severity.CRITICAL, "Payment authorization failed after gateway retry exhaustion", 940, 502);
            case "database-timeout" -> event("database-service", EventType.TIMEOUT, Severity.CRITICAL, "Connection pool timeout while waiting for primary database", 1800, 504);
            case "latency-spike" -> event(SERVICES.get(random.nextInt(SERVICES.size())), EventType.LATENCY, Severity.WARN, "p95 latency breached SLO threshold", 1250 + random.nextInt(900), 200);
            case "5xx-burst" -> event("order-service", EventType.ERROR, Severity.ERROR, "Downstream dependency returned repeated HTTP 5xx responses", 620, 500 + random.nextInt(4));
            default -> random.nextDouble() < 0.35 ? failureEvent() : healthyEvent();
        };
    }

    private ServiceEvent healthyEvent() {
        String service = SERVICES.get(random.nextInt(SERVICES.size()));
        return event(service, EventType.LOG, Severity.INFO, service + " processed request successfully", 80 + random.nextInt(260), 200);
    }

    private ServiceEvent failureEvent() {
        String service = SERVICES.get(random.nextInt(SERVICES.size()));
        if ("payment-service".equals(service)) {
            return event(service, EventType.ERROR, Severity.ERROR, "Card network response delayed; payment retry queued", 870, 502);
        }
        if ("database-service".equals(service)) {
            return event(service, EventType.TIMEOUT, Severity.CRITICAL, "Read replica lag caused database timeout", 1900, 504);
        }
        return event(service, EventType.ERROR, Severity.ERROR, "Unhandled service exception in request path", 650, 500);
    }

    private ServiceEvent event(String service, EventType type, Severity severity, String message, int latency, int status) {
        ServiceEvent event = new ServiceEvent();
        event.setServiceName(service);
        event.setEventType(type);
        event.setSeverity(severity);
        event.setMessage(message);
        event.setLatencyMs(latency);
        event.setStatusCode(status);
        event.setTraceId(UUID.randomUUID().toString());
        event.setTimestamp(Instant.now());
        return event;
    }
}
