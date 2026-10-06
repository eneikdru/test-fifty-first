package com.eneik.generated.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SlotHoldRepository extends JpaRepository<SlotHold, String> {

    Optional<SlotHold> findBySlotId(String slotId);

    void deleteBySlotId(String slotId);
}
