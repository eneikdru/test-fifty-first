package com.eneik.generated.availability;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class AvailabilitySlotService {

    private final AvailabilitySlotRepository slotRepository;
    private final SlotStatusHistoryRepository historyRepository;
    private final Clock clock;

    @Autowired
    public AvailabilitySlotService(
            AvailabilitySlotRepository slotRepository,
            SlotStatusHistoryRepository historyRepository) {
        this(slotRepository, historyRepository, Clock.systemUTC());
    }

    public AvailabilitySlotService(
            AvailabilitySlotRepository slotRepository,
            SlotStatusHistoryRepository historyRepository,
            Clock clock) {
        this.slotRepository = slotRepository;
        this.historyRepository = historyRepository;
        this.clock = clock;
    }

    @Transactional
    public AvailabilitySlot createSlot(String masterId, String serviceId, Instant startTime, Instant endTime) {
        Instant now = clock.instant();
        AvailabilitySlot slot = new AvailabilitySlot(masterId, serviceId, startTime, endTime);
        slot.setCreatedAt(now);
        slot.setUpdatedAt(now);
        AvailabilitySlot saved = slotRepository.save(slot);

        historyRepository.save(new SlotStatusHistory(
                saved.getId(),
                null,
                SlotStatus.FREE,
                "SYSTEM",
                "Time slot created",
                now
        ));

        return saved;
    }

    @Transactional
    public AvailabilitySlot holdSlot(Long slotId, String heldBy, Duration holdDuration) {
        AvailabilitySlot slot = getSlot(slotId);
        if (slot.getStatus() != SlotStatus.FREE) {
            throw new SlotStateConflictException("Cannot hold slot " + slotId + " in status " + slot.getStatus());
        }

        Instant now = clock.instant();
        Instant heldUntil = now.plus(holdDuration);

        int updated = slotRepository.holdSlotAtomically(
                slotId,
                SlotStatus.FREE,
                SlotStatus.HELD,
                heldBy,
                heldUntil,
                now
        );

        if (updated == 0) {
            throw new SlotStateConflictException("Slot " + slotId + " state conflict during hold operation");
        }

        historyRepository.save(new SlotStatusHistory(
                slotId,
                SlotStatus.FREE,
                SlotStatus.HELD,
                heldBy,
                "Slot hold lock acquired until " + heldUntil,
                now
        ));

        return getSlot(slotId);
    }

    @Transactional
    public AvailabilitySlot bookSlot(Long slotId, String bookingId, String bookedBy) {
        AvailabilitySlot slot = getSlot(slotId);
        SlotStatus currentStatus = slot.getStatus();

        if (currentStatus != SlotStatus.FREE && currentStatus != SlotStatus.HELD) {
            throw new SlotStateConflictException("Cannot book slot " + slotId + " in status " + currentStatus);
        }

        Instant now = clock.instant();
        int updated = slotRepository.bookSlotAtomically(
                slotId,
                currentStatus,
                SlotStatus.BOOKED,
                bookingId,
                now
        );

        if (updated == 0) {
            throw new SlotStateConflictException("Slot " + slotId + " state conflict during booking operation");
        }

        historyRepository.save(new SlotStatusHistory(
                slotId,
                currentStatus,
                SlotStatus.BOOKED,
                bookedBy,
                "Slot booked with booking ID " + bookingId,
                now
        ));

        return getSlot(slotId);
    }

    @Transactional
    public AvailabilitySlot markNoShow(Long slotId, String reason, String updatedBy) {
        AvailabilitySlot slot = getSlot(slotId);
        SlotStatus currentStatus = slot.getStatus();

        if (currentStatus != SlotStatus.BOOKED) {
            throw new SlotStateConflictException("Cannot mark no-show for slot " + slotId + " in status " + currentStatus);
        }

        Instant now = clock.instant();
        int updated = slotRepository.updateSlotStatusAtomically(
                slotId,
                SlotStatus.BOOKED,
                SlotStatus.NO_SHOW,
                now
        );

        if (updated == 0) {
            throw new SlotStateConflictException("Slot " + slotId + " state conflict during no-show update");
        }

        String historyReason = (reason != null && !reason.isBlank()) ? reason : "Client no-show recorded";
        historyRepository.save(new SlotStatusHistory(
                slotId,
                SlotStatus.BOOKED,
                SlotStatus.NO_SHOW,
                updatedBy,
                historyReason,
                now
        ));

        return getSlot(slotId);
    }

    public AvailabilitySlot getSlot(Long slotId) {
        return slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException(slotId));
    }

    public List<SlotStatusHistory> getSlotHistory(Long slotId) {
        if (!slotRepository.existsById(slotId)) {
            throw new SlotNotFoundException(slotId);
        }
        return historyRepository.findBySlotIdOrderByChangedAtAsc(slotId);
    }

    public List<AvailabilitySlot> findSlotsByMasterAndTimeRange(String masterId, Instant start, Instant end) {
        return slotRepository.findByMasterIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(masterId, start, end);
    }

    public List<AvailabilitySlot> findSlotsByMasterAndStatus(String masterId, SlotStatus status) {
        return slotRepository.findByMasterIdAndStatus(masterId, status);
    }
}
