package com.eneik.generated.analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SystemMetricRepository extends JpaRepository<SystemMetric, Long> {

    List<SystemMetric> findByMetricName(String metricName);

    Optional<SystemMetric> findFirstByMetricNameOrderByRecordedAtDesc(String metricName);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE system_metrics SET metric_value = metric_value + :delta, recorded_at = :recordedAt WHERE metric_name = :metricName", nativeQuery = true)
    int incrementMetricAtomically(@Param("metricName") String metricName, @Param("delta") double delta, @Param("recordedAt") OffsetDateTime recordedAt);
}
