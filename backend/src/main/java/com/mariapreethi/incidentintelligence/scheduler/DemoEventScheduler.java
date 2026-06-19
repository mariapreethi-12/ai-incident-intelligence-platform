package com.mariapreethi.incidentintelligence.scheduler;

import com.mariapreethi.incidentintelligence.config.AppProperties;
import com.mariapreethi.incidentintelligence.service.EventGenerationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DemoEventScheduler {
    private final AppProperties properties;
    private final EventGenerationService generationService;

    public DemoEventScheduler(AppProperties properties, EventGenerationService generationService) {
        this.properties = properties;
        this.generationService = generationService;
    }

    @Scheduled(fixedDelay = 7000, initialDelay = 3000)
    public void simulate() {
        if (properties.demoMode() && !properties.kafkaEnabled()) {
            generationService.generate("mixed", 3);
        }
    }
}
