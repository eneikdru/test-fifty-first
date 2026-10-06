package com.eneik.generated.availability;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class AvailabilitySlotIntegrationTest {

    @Autowired
    private AvailabilitySlotService slotService;

    @Autowired
    private SlotStatusHistoryRepository historyRepository;

    @Autowired
    private AvailabilitySlotRepository slotRepository;

    @Test
    void testSlotLifecycleWithFullHistoryAndNoShowMaintenance() {
        Instant startTime = Instant.parse("2026-10-10T10:00:00Z");
        Instant endTime = Instant.parse("2026-10-10T11:00:00Z");

        // 1. Create slot (FREE)
        AvailabilitySlot slot = slotService.createSlot("master-101", "service-grooming", startTime, endTime);
        assertNotNull(slot.getId());
        assertEquals(SlotStatus.FREE, slot.getStatus());

        // 2. Hold slot (HELD)
        AvailabilitySlot heldSlot = slotService.holdSlot(slot.getId(), "client-777", Duration.ofMinutes(15));
        assertEquals(SlotStatus.HELD, heldSlot.getStatus());
        assertEquals("client-777", heldSlot.getHeldBy());
        assertNotNull(heldSlot.getHeldUntil());

        // 3. Book slot (BOOKED)
        AvailabilitySlot bookedSlot = slotService.bookSlot(slot.getId(), "booking-abc-123", "client-777");
        assertEquals(SlotStatus.BOOKED, bookedSlot.getStatus());
        assertEquals("booking-abc-123", bookedSlot.getBookingId());

        // 4. Mark No-Show (NO_SHOW)
        AvailabilitySlot noShowSlot = slotService.markNoShow(slot.getId(), "Client did not attend appointment", "master-101");
        assertEquals(SlotStatus.NO_SHOW, noShowSlot.getStatus());

        // 5. Verify complete history audit trail is maintained in DB
        List<SlotStatusHistory> history = slotService.getSlotHistory(slot.getId());
        assertEquals(4, history.size());

        assertEquals(SlotStatus.FREE, history.get(0).getNewStatus());
        assertNull(history.get(0).getPreviousStatus());

        assertEquals(SlotStatus.FREE, history.get(1).getPreviousStatus());
        assertEquals(SlotStatus.HELD, history.get(1).getNewStatus());
        assertEquals("client-777", history.get(1).getChangedBy());

        assertEquals(SlotStatus.HELD, history.get(2).getPreviousStatus());
        assertEquals(SlotStatus.BOOKED, history.get(2).getNewStatus());

        assertEquals(SlotStatus.BOOKED, history.get(3).getPreviousStatus());
        assertEquals(SlotStatus.NO_SHOW, history.get(3).getNewStatus());
        assertEquals("master-101", history.get(3).getChangedBy());
        assertEquals("Client did not attend appointment", history.get(3).getReason());
    }

    @Test
    void testAtomicGuardPreventsConcurrentStateTransitions() {
        Instant startTime = Instant.parse("2026-10-10T12:00:00Z");
        Instant endTime = Instant.parse("2026-10-10T13:00:00Z");

        AvailabilitySlot slot = slotService.createSlot("master-102", "service-haircut", startTime, endTime);

        // First hold succeeds
        slotService.holdSlot(slot.getId(), "client-1", Duration.ofMinutes(10));

        // Attempting to hold again when status is already HELD throws conflict
        assertThrows(SlotStateConflictException.class, () ->
                slotService.holdSlot(slot.getId(), "client-2", Duration.ofMinutes(10))
        );
    }
}
