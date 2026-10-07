package com.eneik.generated.repository;

import com.eneik.generated.model.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {

    List<AvailabilitySlot> findByMasterId(Long masterId);

    List<AvailabilitySlot> findByMasterIdAndStatus(Long masterId, String status);

    @Modifying
    @Query("UPDATE AvailabilitySlot s SET s.status = :newStatus WHERE s.id = :slotId AND s.status = :expectedStatus")
    int updateStatusAtomically(@Param("slotId") Long slotId,
                               @Param("expectedStatus") String expectedStatus,
                               @Param("newStatus") String newStatus);
}
