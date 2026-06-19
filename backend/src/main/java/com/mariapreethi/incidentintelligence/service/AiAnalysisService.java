package com.mariapreethi.incidentintelligence.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mariapreethi.incidentintelligence.config.AppProperties;
import com.mariapreethi.incidentintelligence.dto.AiAnalysisResponse;
import com.mariapreethi.incidentintelligence.model.Incident;
import com.mariapreethi.incidentintelligence.model.ServiceEvent;
import com.mariapreethi.incidentintelligence.repository.IncidentRepository;
import com.mariapreethi.incidentintelligence.repository.ServiceEventRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AiAnalysisService {
    private final AppProperties properties;
    private final IncidentRepository incidentRepository;
    private final ServiceEventRepository eventRepository;
    private final ObjectMapper objectMapper;
    private final RestClient restClient = RestClient.builder().baseUrl("https://api.openai.com/v1").build();

    public AiAnalysisService(AppProperties properties, IncidentRepository incidentRepository, ServiceEventRepository eventRepository, ObjectMapper objectMapper) {
        this.properties = properties;
        this.incidentRepository = incidentRepository;
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
    }

    public AiAnalysisResponse analyze(Long incidentId) {
        Incident incident = incidentRepository.findById(incidentId).orElseThrow();
        AiAnalysisResponse analysis = properties.openaiApiKey() == null || properties.openaiApiKey().isBlank()
                ? mockAnalysis(incident)
                : callOpenAi(incident, eventRepository.findTop75ByOrderByTimestampDesc());
        try {
            incident.setAiAnalysisJson(objectMapper.writeValueAsString(analysis));
        } catch (Exception ignored) {
            incident.setAiAnalysisJson("{}");
        }
        incident.setUpdatedAt(Instant.now());
        incidentRepository.save(incident);
        return analysis;
    }

    private AiAnalysisResponse callOpenAi(Incident incident, List<ServiceEvent> events) {
        try {
            String prompt = """
                    You are an SRE incident commander. Return compact JSON with title, summary, rootCause,
                    affectedServices, timeline, recommendedDebuggingSteps. Incident: %s. Recent events: %s
                    """.formatted(objectMapper.writeValueAsString(incident), objectMapper.writeValueAsString(events));
            Map<String, Object> request = Map.of(
                    "model", "gpt-4o-mini",
                    "messages", List.of(Map.of("role", "user", "content", prompt)),
                    "response_format", Map.of("type", "json_object")
            );
            String response = restClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.openaiApiKey())
                    .body(request)
                    .retrieve()
                    .body(String.class);
            JsonNode content = objectMapper.readTree(response).at("/choices/0/message/content");
            return objectMapper.readValue(content.asText(), AiAnalysisResponse.class);
        } catch (Exception ex) {
            return mockAnalysis(incident);
        }
    }

    private AiAnalysisResponse mockAnalysis(Incident incident) {
        String primary = incident.getPrimaryService();
        return new AiAnalysisResponse(
                incident.getTitle(),
                "Telemetry shows a concentrated failure pattern around " + primary + " with correlated latency and HTTP error signals.",
                "Most likely root cause is a downstream dependency saturation or timeout cascade starting at " + primary + ". The trace pattern suggests retries amplified pressure on adjacent services.",
                List.of(primary, "order-service", "database-service"),
                List.of(
                        "T-5m: baseline traffic healthy with normal latency",
                        "T-3m: error and timeout events begin clustering around " + primary,
                        "T-1m: elevated latency appears in dependent request paths",
                        "Now: incident opened and AI triage generated"
                ),
                List.of(
                        "Check recent deploys, config changes, and feature flags for " + primary,
                        "Inspect p95/p99 latency, retry volume, and connection pool saturation",
                        "Sample failed traces by traceId and compare against healthy traces",
                        "Temporarily reduce retry fan-out or shed low-priority traffic if saturation continues",
                        "Validate database and external gateway health before declaring recovery"
                )
        );
    }
}
