package com.eneik.generated.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, String> {

    List<TimeSlot> findByMasterIdAndStatus(String masterId, SlotStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE TimeSlot t SET t.status = :newStatus, t.updatedAt = :updatedAt, t.version = t.version + 1 " +
           "WHERE t.id = :id AND t.status = :expectedStatus")
    int updateStatusAtomically(@Param("id") String id,
                               @Param("expectedStatus") SlotStatus expectedStatus,
                               @Param("newStatus") SlotStatus newStatus,
                               @Param("updatedAt") Instant updatedAt);
}
