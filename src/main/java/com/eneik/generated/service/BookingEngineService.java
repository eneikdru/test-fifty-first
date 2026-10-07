package com.eneik.generated.service;

import com.eneik.generated.model.AvailabilitySlot;
import com.eneik.generated.model.BookingEntity;
import com.eneik.generated.model.MasterProfile;
import com.eneik.generated.repository.AvailabilitySlotRepository;
import com.eneik.generated.repository.BookingRepository;
import com.eneik.generated.repository.MasterProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingEngineService {

    private final BookingRepository bookingRepository;
    private final AvailabilitySlotRepository slotRepository;
    private final MasterProfileRepository masterRepository;
    private final Clock clock;

    public BookingEngineService(BookingRepository bookingRepository,
                                AvailabilitySlotRepository slotRepository,
                                MasterProfileRepository masterRepository) {
        this(bookingRepository, slotRepository, masterRepository, Clock.systemUTC());
    }

    @Autowired
    public BookingEngineService(BookingRepository bookingRepository,
                                AvailabilitySlotRepository slotRepository,
                                MasterProfileRepository masterRepository,
                                Clock clock) {
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
        this.masterRepository = masterRepository;
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    public List<MasterProfile> searchMasters(String city) {
        if (city == null || city.isBlank()) {
            return masterRepository.findAll();
        }
        return masterRepository.findByCityIgnoreCase(city);
    }

    public List<AvailabilitySlot> getMasterSlots(Long masterId, String status) {
        if (status == null || status.isBlank()) {
            return slotRepository.findByMasterId(masterId);
        }
        return slotRepository.findByMasterIdAndStatus(masterId, status.toUpperCase());
    }

    @Transactional
    public BookingEntity createHold(Long slotId, Long masterId, String customerPhone, String serviceName, Double priceGel) {
        AvailabilitySlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found: " + slotId));

        if (!slot.getMasterId().equals(masterId)) {
            throw new IllegalArgumentException("Slot " + slotId + " does not belong to master " + masterId);
        }

        // Atomically transition slot status from AVAILABLE -> HELD
        int updatedRows = slotRepository.updateStatusAtomically(slotId, "AVAILABLE", "HELD");
        if (updatedRows == 0) {
            throw new SlotUnavailableException("Slot " + slotId + " is no longer available for hold");
        }

        OffsetDateTime now = OffsetDateTime.now(clock);
        OffsetDateTime expiresAt = now.plusMinutes(15);
        String bookingId = UUID.randomUUID().toString();

        BookingEntity booking = new BookingEntity(
                bookingId,
                slotId,
                masterId,
                customerPhone,
                serviceName,
                priceGel,
                "HELD",
                expiresAt,
                now,
                now
        );

        return bookingRepository.save(booking);
    }

    @Transactional
    public BookingEntity confirmBooking(String bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        OffsetDateTime now = OffsetDateTime.now(clock);

        // Atomically transition booking status from HELD -> CONFIRMED
        int updatedRows = bookingRepository.updateStatusAtomically(bookingId, "HELD", "CONFIRMED", now);
        if (updatedRows == 0) {
            throw new IllegalStateException("Booking " + bookingId + " cannot be confirmed in status " + booking.getStatus());
        }

        // Atomically lock slot status HELD -> LOCKED
        slotRepository.updateStatusAtomically(booking.getSlotId(), "HELD", "LOCKED");

        booking.setStatus("CONFIRMED");
        booking.setUpdatedAt(now);
        return booking;
    }

    public Optional<BookingEntity> getBooking(String bookingId) {
        return bookingRepository.findById(bookingId);
    }

    public static class SlotUnavailableException extends RuntimeException {
        public SlotUnavailableException(String message) {
            super(message);
        }
    }
}
