package com.mariapreethi.incidentintelligence.controller;

import com.mariapreethi.incidentintelligence.dto.EventRequest;
import com.mariapreethi.incidentintelligence.model.ServiceEvent;
import com.mariapreethi.incidentintelligence.repository.ServiceEventRepository;
import com.mariapreethi.incidentintelligence.service.EventGenerationService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final ServiceEventRepository eventRepository;
    private final EventGenerationService generationService;

    public EventController(ServiceEventRepository eventRepository, EventGenerationService generationService) {
        this.eventRepository = eventRepository;
        this.generationService = generationService;
    }

    @GetMapping("/latest")
    public List<ServiceEvent> latest() {
        return eventRepository.findTop75ByOrderByTimestampDesc();
    }

    @PostMapping("/generate")
    public List<ServiceEvent> generate(@RequestBody(required = false) EventRequest request) {
        return generationService.generate(request == null ? "mixed" : request.scenario(), 8);
    }
}
