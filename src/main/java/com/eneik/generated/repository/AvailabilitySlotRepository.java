package com.eneik.generated.repository;

import com.eneik.generated.model.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {
    List<AvailabilitySlot> findByMasterId(Long masterId);
    List<AvailabilitySlot> findByMasterIdAndStatus(Long masterId, String status);
}
