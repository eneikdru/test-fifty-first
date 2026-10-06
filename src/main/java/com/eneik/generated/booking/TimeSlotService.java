package com.eneik.generated.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final SlotHoldRepository slotHoldRepository;
    private final SlotStatusHistoryRepository historyRepository;
    private final Clock clock;
    private final Supplier<String> idGenerator;

    @Autowired
    public TimeSlotService(TimeSlotRepository timeSlotRepository,
                           SlotHoldRepository slotHoldRepository,
                           SlotStatusHistoryRepository historyRepository) {
        this(timeSlotRepository, slotHoldRepository, historyRepository, Clock.systemUTC(), () -> UUID.randomUUID().toString());
    }

    public TimeSlotService(TimeSlotRepository timeSlotRepository,
                           SlotHoldRepository slotHoldRepository,
                           SlotStatusHistoryRepository historyRepository,
                           Clock clock,
                           Supplier<String> idGenerator) {
        this.timeSlotRepository = timeSlotRepository;
        this.slotHoldRepository = slotHoldRepository;
        this.historyRepository = historyRepository;
        this.clock = clock;
        this.idGenerator = idGenerator;
    }

    @Transactional
    public TimeSlot createSlot(String masterId, Instant startTime, Instant endTime) {
        Instant now = clock.instant();
        String id = idGenerator.get();
        TimeSlot slot = new TimeSlot(id, masterId, startTime, endTime, SlotStatus.FREE, now, now);
        TimeSlot saved = timeSlotRepository.save(slot);

        recordHistory(saved.getId(), null, SlotStatus.FREE, "Slot created", now);
        return saved;
    }

    @Transactional
    public boolean holdSlot(String slotId, String heldBy, Duration duration) {
        Instant now = clock.instant();
        Optional<TimeSlot> optionalSlot = timeSlotRepository.findById(slotId);
        if (optionalSlot.isEmpty()) {
            return false;
        }

        TimeSlot slot = optionalSlot.get();
        if (!slot.getStatus().canTransitionTo(SlotStatus.HELD)) {
            return false;
        }

        int updatedRows = timeSlotRepository.updateStatusAtomically(slotId, SlotStatus.FREE, SlotStatus.HELD, now);
        if (updatedRows == 0) {
            return false;
        }

        Instant expiresAt = now.plus(duration);
        SlotHold hold = new SlotHold(idGenerator.get(), slotId, heldBy, expiresAt, now);
        slotHoldRepository.save(hold);

        recordHistory(slotId, SlotStatus.FREE, SlotStatus.HELD, "Slot held by " + heldBy, now);
        return true;
    }

    @Transactional
    public boolean confirmBooking(String slotId, String heldBy) {
        Instant now = clock.instant();
        Optional<TimeSlot> optionalSlot = timeSlotRepository.findById(slotId);
        if (optionalSlot.isEmpty()) {
            return false;
        }

        TimeSlot slot = optionalSlot.get();
        if (!slot.getStatus().canTransitionTo(SlotStatus.BOOKED)) {
            return false;
        }

        int updatedRows = timeSlotRepository.updateStatusAtomically(slotId, SlotStatus.HELD, SlotStatus.BOOKED, now);
        if (updatedRows == 0) {
            return false;
        }

        slotHoldRepository.deleteBySlotId(slotId);
        recordHistory(slotId, SlotStatus.HELD, SlotStatus.BOOKED, "Booking confirmed for " + heldBy, now);
        return true;
    }

    @Transactional
    public boolean markNoShow(String slotId, String reason) {
        Instant now = clock.instant();
        Optional<TimeSlot> optionalSlot = timeSlotRepository.findById(slotId);
        if (optionalSlot.isEmpty()) {
            return false;
        }

        TimeSlot slot = optionalSlot.get();
        if (!slot.getStatus().canTransitionTo(SlotStatus.NO_SHOW)) {
            return false;
        }

        int updatedRows = timeSlotRepository.updateStatusAtomically(slotId, SlotStatus.BOOKED, SlotStatus.NO_SHOW, now);
        if (updatedRows == 0) {
            return false;
        }

        recordHistory(slotId, SlotStatus.BOOKED, SlotStatus.NO_SHOW, reason != null ? reason : "No show recorded", now);
        return true;
    }

    @Transactional
    public boolean cancelSlot(String slotId, String reason) {
        Instant now = clock.instant();
        Optional<TimeSlot> optionalSlot = timeSlotRepository.findById(slotId);
        if (optionalSlot.isEmpty()) {
            return false;
        }

        TimeSlot slot = optionalSlot.get();
        SlotStatus currentStatus = slot.getStatus();
        if (!currentStatus.canTransitionTo(SlotStatus.CANCELLED)) {
            return false;
        }

        int updatedRows = timeSlotRepository.updateStatusAtomically(slotId, currentStatus, SlotStatus.CANCELLED, now);
        if (updatedRows == 0) {
            return false;
        }

        if (currentStatus == SlotStatus.HELD) {
            slotHoldRepository.deleteBySlotId(slotId);
        }

        recordHistory(slotId, currentStatus, SlotStatus.CANCELLED, reason != null ? reason : "Slot cancelled", now);
        return true;
    }

    public List<SlotStatusHistory> getSlotHistory(String slotId) {
        return historyRepository.findBySlotIdOrderByChangedAtAsc(slotId);
    }

    private void recordHistory(String slotId, SlotStatus previousStatus, SlotStatus newStatus, String reason, Instant timestamp) {
        SlotStatusHistory history = new SlotStatusHistory(
                idGenerator.get(),
                slotId,
                previousStatus,
                newStatus,
                reason,
                timestamp
        );
        historyRepository.save(history);
    }
}
