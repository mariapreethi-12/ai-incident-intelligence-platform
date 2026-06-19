package com.mariapreethi.incidentintelligence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AiIncidentIntelligenceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiIncidentIntelligenceApplication.class, args);
    }
}
