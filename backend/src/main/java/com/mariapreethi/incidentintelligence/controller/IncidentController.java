package com.mariapreethi.incidentintelligence.controller;

import com.mariapreethi.incidentintelligence.dto.AiAnalysisResponse;
import com.mariapreethi.incidentintelligence.model.Incident;
import com.mariapreethi.incidentintelligence.repository.IncidentRepository;
import com.mariapreethi.incidentintelligence.service.AiAnalysisService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {
    private final IncidentRepository incidentRepository;
    private final AiAnalysisService aiAnalysisService;

    public IncidentController(IncidentRepository incidentRepository, AiAnalysisService aiAnalysisService) {
        this.incidentRepository = incidentRepository;
        this.aiAnalysisService = aiAnalysisService;
    }

    @GetMapping
    public List<Incident> incidents() {
        return incidentRepository.findTop50ByOrderByCreatedAtDesc();
    }

    @GetMapping("/{id}")
    public Incident incident(@PathVariable Long id) {
        return incidentRepository.findById(id).orElseThrow();
    }

    @PostMapping("/{id}/analyze")
    public AiAnalysisResponse analyze(@PathVariable Long id) {
        return aiAnalysisService.analyze(id);
    }
}
