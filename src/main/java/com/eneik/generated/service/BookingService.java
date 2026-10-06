package com.eneik.generated.service;

import com.eneik.generated.domain.AvailabilitySlot;
import com.eneik.generated.domain.AvailabilitySlotRepository;
import com.eneik.generated.domain.Booking;
import com.eneik.generated.domain.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;

@Service
public class BookingService {

    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final BookingRepository bookingRepository;
    private final TelemetryService telemetryService;
    private final Clock clock;

    public BookingService(AvailabilitySlotRepository availabilitySlotRepository,
                          BookingRepository bookingRepository,
                          TelemetryService telemetryService) {
        this(availabilitySlotRepository, bookingRepository, telemetryService, Clock.systemUTC());
    }

    @Autowired
    public BookingService(AvailabilitySlotRepository availabilitySlotRepository,
                          BookingRepository bookingRepository,
                          TelemetryService telemetryService,
                          Clock clock) {
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.bookingRepository = bookingRepository;
        this.telemetryService = telemetryService;
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    @Transactional
    public Booking processBooking(String masterId, Long slotId, String customerName, String customerPhone) {
        AvailabilitySlot slot = availabilitySlotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found: " + slotId));

        if (!slot.getMasterId().equals(masterId)) {
            throw new IllegalArgumentException("Slot " + slotId + " does not belong to master " + masterId);
        }

        int updatedCount = availabilitySlotRepository.markSlotAsBookedAtomically(slotId);
        if (updatedCount == 0) {
            throw new IllegalStateException("Slot " + slotId + " is already booked or unavailable.");
        }

        OffsetDateTime now = OffsetDateTime.now(clock);
        String bookingRef = "BKG-" + masterId + "-" + slotId + "-" + now.toEpochSecond();

        Booking booking = new Booking(
                bookingRef,
                masterId,
                slotId,
                customerName,
                customerPhone,
                "CONFIRMED",
                now
        );

        Booking savedBooking = bookingRepository.save(booking);

        telemetryService.recordEvent(
                "BOOKING_PROCESSED",
                "BOOKING",
                savedBooking.getBookingReference(),
                "{\"masterId\":\"" + masterId + "\",\"slotId\":" + slotId + ",\"priceGEL\":" + slot.getPriceGEL() + "}"
        );

        telemetryService.incrementGqmBookingConversion();

        return savedBooking;
    }
}
