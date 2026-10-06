package com.eneik.generated.availability;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SlotStatusHistoryRepository extends JpaRepository<SlotStatusHistory, Long> {

    List<SlotStatusHistory> findBySlotIdOrderByChangedAtAsc(Long slotId);
}
