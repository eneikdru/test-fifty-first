package com.eneik.generated.analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface SystemMetricRepository extends JpaRepository<SystemMetric, Long> {
    List<SystemMetric> findByMetricName(String metricName);

    @Modifying
    @Query(value = "UPDATE system_metrics SET metric_value = metric_value + :amount, recorded_at = :recordedAt WHERE metric_name = :metricName", nativeQuery = true)
    int incrementMetricValue(@Param("metricName") String metricName, @Param("amount") double amount, @Param("recordedAt") OffsetDateTime recordedAt);
}
