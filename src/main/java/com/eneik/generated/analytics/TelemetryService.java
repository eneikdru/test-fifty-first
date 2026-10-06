package com.eneik.generated.analytics;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class TelemetryService {

    public static final String GQM_CONVERSION_METRIC = "gqm_conversion_count";

    private final SystemMetricRepository systemMetricRepository;
    private final AnalyticsEventRepository analyticsEventRepository;
    private final EntityManager entityManager;

    public TelemetryService(SystemMetricRepository systemMetricRepository,
                            AnalyticsEventRepository analyticsEventRepository,
                            EntityManager entityManager) {
        this.systemMetricRepository = systemMetricRepository;
        this.analyticsEventRepository = analyticsEventRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public SystemMetric recordMetric(String metricName, double value, String unit, String tags) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        SystemMetric metric = new SystemMetric(metricName, value, unit, tags, now);
        return systemMetricRepository.save(metric);
    }

    @Transactional
    public SystemMetric processBookingEvent(String bookingId, String status, String details) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        // Record raw event
        AnalyticsEvent event = new AnalyticsEvent("BOOKING_PROCESSED", "BOOKING", bookingId, details, now);
        analyticsEventRepository.save(event);

        // Atomically increment GQM conversion metric if it exists, otherwise create it
        int updatedCount = systemMetricRepository.incrementMetricValue(GQM_CONVERSION_METRIC, 1.0, now);
        if (updatedCount == 0) {
            try {
                SystemMetric newMetric = new SystemMetric(GQM_CONVERSION_METRIC, 1.0, "count", "gqm=conversion", now);
                systemMetricRepository.saveAndFlush(newMetric);
            } catch (Exception e) {
                systemMetricRepository.incrementMetricValue(GQM_CONVERSION_METRIC, 1.0, now);
            }
        } else {
            entityManager.clear(); // Clear persistence context so next query fetches fresh DB state
        }

        List<SystemMetric> metrics = systemMetricRepository.findByMetricName(GQM_CONVERSION_METRIC);
        return metrics.isEmpty() ? null : metrics.get(0);
    }

    public List<SystemMetric> getMetrics(String name) {
        if (name != null && !name.isBlank()) {
            return systemMetricRepository.findByMetricName(name);
        }
        return systemMetricRepository.findAll();
    }
}
