package com.eneik.generated.service;

import com.eneik.generated.analytics.AnalyticsEvent;
import com.eneik.generated.analytics.AnalyticsEventRepository;
import com.eneik.generated.analytics.SystemMetric;
import com.eneik.generated.analytics.SystemMetricRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class TelemetryService {

    public static final String GQM_CONVERSION_METRIC_NAME = "gqm_booking_conversion";

    private final AnalyticsEventRepository analyticsEventRepository;
    private final SystemMetricRepository systemMetricRepository;
    private final Clock clock;

    public TelemetryService(AnalyticsEventRepository analyticsEventRepository,
                            SystemMetricRepository systemMetricRepository,
                            Clock clock) {
        this.analyticsEventRepository = analyticsEventRepository;
        this.systemMetricRepository = systemMetricRepository;
        this.clock = clock;
    }

    @Transactional
    public AnalyticsEvent recordEvent(String eventType, String entityType, String entityId, String payload) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        AnalyticsEvent event = new AnalyticsEvent(eventType, entityType, entityId, payload, now);
        return analyticsEventRepository.save(event);
    }

    @Transactional
    public SystemMetric recordMetric(String metricName, double value, String unit, String tags) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        SystemMetric metric = new SystemMetric(metricName, value, unit, tags, now);
        return systemMetricRepository.save(metric);
    }

    @Transactional
    public double incrementGqmBookingConversion() {
        OffsetDateTime now = OffsetDateTime.now(clock);
        int updated = systemMetricRepository.incrementMetricAtomically(GQM_CONVERSION_METRIC_NAME, 1.0, now);
        if (updated == 0) {
            SystemMetric initial = new SystemMetric(GQM_CONVERSION_METRIC_NAME, 1.0, "count", "category=gqm_conversion", now);
            systemMetricRepository.save(initial);
            return 1.0;
        }
        return getGqmBookingConversionValue();
    }

    @Transactional(readOnly = true)
    public double getGqmBookingConversionValue() {
        return systemMetricRepository.findFirstByMetricNameOrderByRecordedAtDesc(GQM_CONVERSION_METRIC_NAME)
                .map(SystemMetric::getMetricValue)
                .orElse(0.0);
    }

    @Transactional(readOnly = true)
    public List<SystemMetric> getAllMetrics() {
        return systemMetricRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<AnalyticsEvent> getEventsByType(String eventType) {
        return analyticsEventRepository.findByEventType(eventType);
    }
}
