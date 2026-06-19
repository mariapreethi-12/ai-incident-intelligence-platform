package com.mariapreethi.incidentintelligence.repository;

import com.mariapreethi.incidentintelligence.model.ServiceEvent;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceEventRepository extends JpaRepository<ServiceEvent, Long> {
    List<ServiceEvent> findTop75ByOrderByTimestampDesc();
    List<ServiceEvent> findByTimestampAfterOrderByTimestampDesc(Instant since);
    long countByStatusCodeGreaterThanEqualAndTimestampAfter(int statusCode, Instant since);
}
