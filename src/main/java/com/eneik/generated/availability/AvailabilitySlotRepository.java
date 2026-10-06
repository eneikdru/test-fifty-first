package com.eneik.generated.availability;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {

    List<AvailabilitySlot> findByMasterIdAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
            String masterId, Instant startTime, Instant endTime);

    List<AvailabilitySlot> findByMasterIdAndStatus(String masterId, SlotStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE AvailabilitySlot s SET s.status = :newStatus, s.heldBy = :heldBy, s.heldUntil = :heldUntil, s.updatedAt = :now, s.version = s.version + 1 WHERE s.id = :id AND s.status = :expectedStatus")
    int holdSlotAtomically(
            @Param("id") Long id,
            @Param("expectedStatus") SlotStatus expectedStatus,
            @Param("newStatus") SlotStatus newStatus,
            @Param("heldBy") String heldBy,
            @Param("heldUntil") Instant heldUntil,
            @Param("now") Instant now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE AvailabilitySlot s SET s.status = :newStatus, s.bookingId = :bookingId, s.updatedAt = :now, s.version = s.version + 1 WHERE s.id = :id AND s.status = :expectedStatus")
    int bookSlotAtomically(
            @Param("id") Long id,
            @Param("expectedStatus") SlotStatus expectedStatus,
            @Param("newStatus") SlotStatus newStatus,
            @Param("bookingId") String bookingId,
            @Param("now") Instant now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE AvailabilitySlot s SET s.status = :newStatus, s.updatedAt = :now, s.version = s.version + 1 WHERE s.id = :id AND s.status = :expectedStatus")
    int updateSlotStatusAtomically(
            @Param("id") Long id,
            @Param("expectedStatus") SlotStatus expectedStatus,
            @Param("newStatus") SlotStatus newStatus,
            @Param("now") Instant now);
}
