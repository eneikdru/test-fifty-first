package com.eneik.generated.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {

    List<AvailabilitySlot> findByMasterId(String masterId);

    List<AvailabilitySlot> findByMasterIdAndIsBookedFalse(String masterId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE AvailabilitySlot s SET s.isBooked = true, s.status = 'BOOKED' WHERE s.id = :id AND s.isBooked = false AND s.status = 'AVAILABLE'")
    int markSlotAsBookedAtomically(@Param("id") Long id);
}
