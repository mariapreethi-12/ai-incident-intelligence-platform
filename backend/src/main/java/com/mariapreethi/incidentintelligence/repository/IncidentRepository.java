package com.mariapreethi.incidentintelligence.repository;

import com.mariapreethi.incidentintelligence.model.Incident;
import com.mariapreethi.incidentintelligence.model.IncidentStatus;
import com.mariapreethi.incidentintelligence.model.Severity;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findTop50ByOrderByCreatedAtDesc();
    long countByStatus(IncidentStatus status);
    long countBySeverity(Severity severity);
    boolean existsByIncidentTypeAndPrimaryServiceAndCreatedAtAfter(String incidentType, String primaryService, Instant createdAt);
}
