package com.eneik.generated.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TimeSlotServiceTest {

    private TimeSlotRepository timeSlotRepository;
    private SlotHoldRepository slotHoldRepository;
    private SlotStatusHistoryRepository historyRepository;
    private Clock fixedClock;
    private Supplier<String> idGenerator;
    private TimeSlotService timeSlotService;

    private final Instant fixedNow = Instant.parse("2026-10-06T12:00:00Z");

    @BeforeEach
    void setUp() {
        timeSlotRepository = mock(TimeSlotRepository.class);
        slotHoldRepository = mock(SlotHoldRepository.class);
        historyRepository = mock(SlotStatusHistoryRepository.class);
        fixedClock = Clock.fixed(fixedNow, ZoneOffset.UTC);

        AtomicInteger counter = new AtomicInteger(100);
        idGenerator = () -> "id-" + counter.getAndIncrement();

        timeSlotService = new TimeSlotService(
                timeSlotRepository,
                slotHoldRepository,
                historyRepository,
                fixedClock,
                idGenerator
        );
    }

    @Test
    void testCreateSlot() {
        Instant start = fixedNow.plus(Duration.ofHours(1));
        Instant end = fixedNow.plus(Duration.ofHours(2));

        when(timeSlotRepository.save(any(TimeSlot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TimeSlot slot = timeSlotService.createSlot("master-1", start, end);

        assertNotNull(slot);
        assertEquals("id-100", slot.getId());
        assertEquals("master-1", slot.getMasterId());
        assertEquals(SlotStatus.FREE, slot.getStatus());
        assertEquals(fixedNow, slot.getCreatedAt());

        ArgumentCaptor<SlotStatusHistory> historyCaptor = ArgumentCaptor.forClass(SlotStatusHistory.class);
        verify(historyRepository).save(historyCaptor.capture());

        SlotStatusHistory history = historyCaptor.getValue();
        assertEquals("id-101", history.getId());
        assertEquals("id-100", history.getSlotId());
        assertEquals(null, history.getPreviousStatus());
        assertEquals(SlotStatus.FREE, history.getNewStatus());
        assertEquals(fixedNow, history.getChangedAt());
    }

    @Test
    void testHoldSlotSuccess() {
        TimeSlot existing = new TimeSlot("slot-1", "master-1", fixedNow.plusSeconds(3600), fixedNow.plusSeconds(7200), SlotStatus.FREE, fixedNow, fixedNow);
        when(timeSlotRepository.findById("slot-1")).thenReturn(Optional.of(existing));
        when(timeSlotRepository.updateStatusAtomically("slot-1", SlotStatus.FREE, SlotStatus.HELD, fixedNow)).thenReturn(1);

        boolean result = timeSlotService.holdSlot("slot-1", "user-10", Duration.ofMinutes(15));

        assertTrue(result);
        verify(slotHoldRepository).save(any(SlotHold.class));
        verify(historyRepository).save(any(SlotStatusHistory.class));
    }

    @Test
    void testHoldSlotAtomicFailureWhenConcurrent() {
        TimeSlot existing = new TimeSlot("slot-1", "master-1", fixedNow.plusSeconds(3600), fixedNow.plusSeconds(7200), SlotStatus.FREE, fixedNow, fixedNow);
        when(timeSlotRepository.findById("slot-1")).thenReturn(Optional.of(existing));
        when(timeSlotRepository.updateStatusAtomically("slot-1", SlotStatus.FREE, SlotStatus.HELD, fixedNow)).thenReturn(0);

        boolean result = timeSlotService.holdSlot("slot-1", "user-10", Duration.ofMinutes(15));

        assertFalse(result);
    }

    @Test
    void testConfirmBooking() {
        TimeSlot existing = new TimeSlot("slot-1", "master-1", fixedNow.plusSeconds(3600), fixedNow.plusSeconds(7200), SlotStatus.HELD, fixedNow, fixedNow);
        when(timeSlotRepository.findById("slot-1")).thenReturn(Optional.of(existing));
        when(timeSlotRepository.updateStatusAtomically("slot-1", SlotStatus.HELD, SlotStatus.BOOKED, fixedNow)).thenReturn(1);

        boolean result = timeSlotService.confirmBooking("slot-1", "user-10");

        assertTrue(result);
        verify(slotHoldRepository).deleteBySlotId("slot-1");

        ArgumentCaptor<SlotStatusHistory> historyCaptor = ArgumentCaptor.forClass(SlotStatusHistory.class);
        verify(historyRepository).save(historyCaptor.capture());

        SlotStatusHistory history = historyCaptor.getValue();
        assertEquals(SlotStatus.HELD, history.getPreviousStatus());
        assertEquals(SlotStatus.BOOKED, history.getNewStatus());
    }

    @Test
    void testMarkNoShowMaintainsHistory() {
        TimeSlot existing = new TimeSlot("slot-1", "master-1", fixedNow.minusSeconds(7200), fixedNow.minusSeconds(3600), SlotStatus.BOOKED, fixedNow, fixedNow);
        when(timeSlotRepository.findById("slot-1")).thenReturn(Optional.of(existing));
        when(timeSlotRepository.updateStatusAtomically("slot-1", SlotStatus.BOOKED, SlotStatus.NO_SHOW, fixedNow)).thenReturn(1);

        boolean result = timeSlotService.markNoShow("slot-1", "Client did not arrive");

        assertTrue(result);

        ArgumentCaptor<SlotStatusHistory> historyCaptor = ArgumentCaptor.forClass(SlotStatusHistory.class);
        verify(historyRepository).save(historyCaptor.capture());

        SlotStatusHistory history = historyCaptor.getValue();
        assertEquals(SlotStatus.BOOKED, history.getPreviousStatus());
        assertEquals(SlotStatus.NO_SHOW, history.getNewStatus());
        assertEquals("Client did not arrive", history.getReason());
        assertEquals(fixedNow, history.getChangedAt());
    }
}
