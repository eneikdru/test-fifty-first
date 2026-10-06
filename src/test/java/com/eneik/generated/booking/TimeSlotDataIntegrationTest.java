package com.eneik.generated.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TimeSlotDataIntegrationTest {

    @Autowired
    private TimeSlotService timeSlotService;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private SlotHoldRepository slotHoldRepository;

    @Autowired
    private SlotStatusHistoryRepository historyRepository;

    @Test
    void testFullSlotLifecycleTransitionsAndHistory() {
        Instant now = Instant.parse("2026-10-06T12:00:00Z");
        Instant start = now.plus(Duration.ofHours(2));
        Instant end = now.plus(Duration.ofHours(3));

        // 1. Create slot (FREE)
        TimeSlot slot = timeSlotService.createSlot("master-batumi-1", start, end);
        assertNotNull(slot.getId());
        assertEquals(SlotStatus.FREE, slot.getStatus());

        // Verify history record for creation
        List<SlotStatusHistory> history1 = historyRepository.findBySlotIdOrderByChangedAtAsc(slot.getId());
        assertEquals(1, history1.size());
        assertEquals(SlotStatus.FREE, history1.get(0).getNewStatus());

        // 2. Place hold on slot (FREE -> HELD)
        boolean held = timeSlotService.holdSlot(slot.getId(), "user-fb-101", Duration.ofMinutes(15));
        assertTrue(held);

        TimeSlot heldSlot = timeSlotRepository.findById(slot.getId()).orElseThrow();
        assertEquals(SlotStatus.HELD, heldSlot.getStatus());
        assertTrue(slotHoldRepository.findBySlotId(slot.getId()).isPresent());

        // 3. Confirm booking (HELD -> BOOKED)
        boolean confirmed = timeSlotService.confirmBooking(slot.getId(), "user-fb-101");
        assertTrue(confirmed);

        TimeSlot bookedSlot = timeSlotRepository.findById(slot.getId()).orElseThrow();
        assertEquals(SlotStatus.BOOKED, bookedSlot.getStatus());
        assertFalse(slotHoldRepository.findBySlotId(slot.getId()).isPresent());

        // 4. Mark no-show (BOOKED -> NO_SHOW) and verify historical record is maintained
        boolean noShow = timeSlotService.markNoShow(slot.getId(), "Customer missed appointment");
        assertTrue(noShow);

        TimeSlot finalSlot = timeSlotRepository.findById(slot.getId()).orElseThrow();
        assertEquals(SlotStatus.NO_SHOW, finalSlot.getStatus());

        // Verify full historical trace maintained
        List<SlotStatusHistory> history = historyRepository.findBySlotIdOrderByChangedAtAsc(slot.getId());
        assertEquals(4, history.size());

        assertEquals(null, history.get(0).getPreviousStatus());
        assertEquals(SlotStatus.FREE, history.get(0).getNewStatus());

        assertEquals(SlotStatus.FREE, history.get(1).getPreviousStatus());
        assertEquals(SlotStatus.HELD, history.get(1).getNewStatus());

        assertEquals(SlotStatus.HELD, history.get(2).getPreviousStatus());
        assertEquals(SlotStatus.BOOKED, history.get(2).getNewStatus());

        assertEquals(SlotStatus.BOOKED, history.get(3).getPreviousStatus());
        assertEquals(SlotStatus.NO_SHOW, history.get(3).getNewStatus());
        assertEquals("Customer missed appointment", history.get(3).getReason());
    }
}
