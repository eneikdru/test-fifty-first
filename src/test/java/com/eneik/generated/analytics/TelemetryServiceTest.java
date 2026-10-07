package com.eneik.generated.analytics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@io.zonky.test.db.AutoConfigureEmbeddedDatabase(type = io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class TelemetryServiceTest {

    @Autowired
    private TelemetryService telemetryService;

    @Autowired
    private SystemMetricRepository systemMetricRepository;

    @Autowired
    private AnalyticsEventRepository analyticsEventRepository;

    @Test
    public void testBookingEventIncrementsGqmMetric() {
        double initialValue = 0.0;
        List<SystemMetric> initialMetrics = systemMetricRepository.findByMetricName(TelemetryService.GQM_CONVERSION_METRIC);
        if (!initialMetrics.isEmpty()) {
            initialValue = initialMetrics.get(0).getMetricValue();
        }

        SystemMetric updatedMetric = telemetryService.processBookingEvent("BK-1001", "CONFIRMED", "GQM booking via voice");

        assertThat(updatedMetric.getMetricName()).isEqualTo(TelemetryService.GQM_CONVERSION_METRIC);
        assertThat(updatedMetric.getMetricValue()).isEqualTo(initialValue + 1.0);

        List<AnalyticsEvent> events = analyticsEventRepository.findByEventType("BOOKING_PROCESSED");
        assertThat(events).isNotEmpty();
        assertThat(events.stream().anyMatch(e -> "BK-1001".equals(e.getEntityId()))).isTrue();

        // Process a second event and verify count increments to initialValue + 2.0
        SystemMetric secondUpdate = telemetryService.processBookingEvent("BK-1002", "CONFIRMED", "GQM booking via web");
        assertThat(secondUpdate.getMetricValue()).isEqualTo(initialValue + 2.0);
    }
}
