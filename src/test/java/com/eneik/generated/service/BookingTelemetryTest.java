package com.eneik.generated.service;

import com.eneik.generated.analytics.AnalyticsEvent;
import com.eneik.generated.analytics.AnalyticsEventRepository;
import com.eneik.generated.domain.AvailabilitySlot;
import com.eneik.generated.domain.AvailabilitySlotRepository;
import com.eneik.generated.domain.Booking;
import com.eneik.generated.domain.MasterProfile;
import com.eneik.generated.domain.MasterProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class BookingTelemetryTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TelemetryService telemetryService;

    @Autowired
    private MasterProfileRepository masterProfileRepository;

    @Autowired
    private AvailabilitySlotRepository availabilitySlotRepository;

    @Autowired
    private AnalyticsEventRepository analyticsEventRepository;

    private final Clock fixedClock = Clock.fixed(Instant.parse("2026-10-06T12:00:00Z"), ZoneOffset.UTC);

    @Test
    @DisplayName("Given a booking event, When processed, Then it correctly increments the GQM conversion metric")
    void testBookingProcessingIncrementsGqmMetric() {
        OffsetDateTime now = OffsetDateTime.now(fixedClock);

        MasterProfile master = new MasterProfile(
                "master-test-1",
                "David Loria",
                "Tbilisi",
                "+995555123456",
                "https://facebook.com/david.barber",
                "Master Barber",
                now
        );
        masterProfileRepository.save(master);

        AvailabilitySlot slot = new AvailabilitySlot(
                master.getId(),
                "Haircut",
                35.0,
                30,
                now.plusHours(1),
                now.plusHours(1).plusMinutes(30),
                false,
                "AVAILABLE"
        );
        AvailabilitySlot savedSlot = availabilitySlotRepository.save(slot);

        double initialMetricValue = telemetryService.getGqmBookingConversionValue();

        Booking booking = bookingService.processBooking(
                master.getId(),
                savedSlot.getId(),
                "Levan Kipiani",
                "+995599000111"
        );

        assertThat(booking).isNotNull();
        assertThat(booking.getBookingReference()).isNotBlank();
        assertThat(booking.getStatus()).isEqualTo("CONFIRMED");

        AvailabilitySlot updatedSlot = availabilitySlotRepository.findById(savedSlot.getId()).orElseThrow();
        assertThat(updatedSlot.isBooked()).isTrue();
        assertThat(updatedSlot.getStatus()).isEqualTo("BOOKED");

        List<AnalyticsEvent> events = analyticsEventRepository.findByEventType("BOOKING_PROCESSED");
        assertThat(events).isNotEmpty();
        assertThat(events.get(0).getPayload()).contains("master-test-1");

        double updatedMetricValue = telemetryService.getGqmBookingConversionValue();
        // Type-safe float comparison with explicit tolerance
        assertThat(updatedMetricValue).isCloseTo(initialMetricValue + 1.0, offset(0.0001));
    }
}
