package com.eneik.generated.analytics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class AnalyticsSchemaTest {

    @Autowired
    private AnalyticsEventRepository analyticsEventRepository;

    @Autowired
    private SystemMetricRepository systemMetricRepository;

    private final Clock fixedClock = Clock.fixed(Instant.parse("2026-10-06T12:00:00Z"), ZoneOffset.UTC);

    @Test
    @DisplayName("Given Flyway migration, When analytics events are saved, Then they can be queried by event type")
    void testAnalyticsEventStorage() {
        OffsetDateTime now = OffsetDateTime.now(fixedClock);
        AnalyticsEvent event = new AnalyticsEvent(
                "BOOKING_CREATED",
                "MASTER_PROFILE",
                "master-123",
                "{\"serviceId\":\"srv-1\",\"priceGEL\":50.0}",
                now
        );

        AnalyticsEvent saved = analyticsEventRepository.save(event);
        assertThat(saved.getId()).isNotNull();

        List<AnalyticsEvent> fetched = analyticsEventRepository.findByEventType("BOOKING_CREATED");
        assertThat(fetched).hasSize(1);
        assertThat(fetched.get(0).getEntityId()).isEqualTo("master-123");
        assertThat(fetched.get(0).getPayload()).contains("srv-1");
    }

    @Test
    @DisplayName("Given system metrics table, When metric is recorded, Then numeric float precision is preserved")
    void testSystemMetricStorage() {
        OffsetDateTime now = OffsetDateTime.now(fixedClock);
        SystemMetric metric = new SystemMetric(
                "cpu_utilization",
                87.65,
                "percent",
                "env=prod,region=tbilisi",
                now
        );

        SystemMetric saved = systemMetricRepository.save(metric);
        assertThat(saved.getId()).isNotNull();

        List<SystemMetric> metrics = systemMetricRepository.findByMetricName("cpu_utilization");
        assertThat(metrics).isNotEmpty();
        SystemMetric retrieved = metrics.get(0);

        // Type-safe float comparison with explicit tolerance
        assertThat(retrieved.getMetricValue()).isCloseTo(87.65, offset(0.0001));
        assertThat(retrieved.getUnit()).isEqualTo("percent");
        assertThat(retrieved.getTags()).contains("tbilisi");
    }
}
