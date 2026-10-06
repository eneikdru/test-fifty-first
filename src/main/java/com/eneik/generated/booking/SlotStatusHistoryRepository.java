package com.eneik.generated.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlotStatusHistoryRepository extends JpaRepository<SlotStatusHistory, String> {

    List<SlotStatusHistory> findBySlotIdOrderByChangedAtAsc(String slotId);
}
