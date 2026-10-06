package com.eneik.generated.availability;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilitySlotServiceTest {

    @Mock
    private AvailabilitySlotRepository slotRepository;

    @Mock
    private SlotStatusHistoryRepository historyRepository;

    private AvailabilitySlotService service;
    private final Instant fixedNow = Instant.parse("2026-10-06T12:00:00Z");
    private final Clock fixedClock = Clock.fixed(fixedNow, ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        service = new AvailabilitySlotService(slotRepository, historyRepository, fixedClock);
    }

    @Test
    void testCreateSlotInitializesFreeStatusAndRecordsHistory() {
        Instant startTime = fixedNow.plus(Duration.ofHours(1));
        Instant endTime = fixedNow.plus(Duration.ofHours(2));

        AvailabilitySlot slotToSave = new AvailabilitySlot("master-1", "service-a", startTime, endTime);
        slotToSave.setCreatedAt(fixedNow);
        slotToSave.setUpdatedAt(fixedNow);

        AvailabilitySlot savedSlot = new AvailabilitySlot("master-1", "service-a", startTime, endTime);
        savedSlot.setId(100L);
        savedSlot.setCreatedAt(fixedNow);
        savedSlot.setUpdatedAt(fixedNow);

        when(slotRepository.save(any(AvailabilitySlot.class))).thenReturn(savedSlot);

        AvailabilitySlot result = service.createSlot("master-1", "service-a", startTime, endTime);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(SlotStatus.FREE, result.getStatus());

        verify(historyRepository, times(1)).save(argThat(history ->
                history.getSlotId().equals(100L) &&
                history.getPreviousStatus() == null &&
                history.getNewStatus() == SlotStatus.FREE &&
                history.getChangedAt().equals(fixedNow)
        ));
    }

    @Test
    void testStateTransitionsFreeToHeldToBooked() {
        Instant startTime = fixedNow.plus(Duration.ofHours(1));
        Instant endTime = fixedNow.plus(Duration.ofHours(2));

        AvailabilitySlot slot = new AvailabilitySlot("master-1", "service-a", startTime, endTime);
        slot.setId(100L);
        slot.setStatus(SlotStatus.FREE);

        when(slotRepository.findById(100L)).thenReturn(Optional.of(slot));
        when(slotRepository.holdSlotAtomically(eq(100L), eq(SlotStatus.FREE), eq(SlotStatus.HELD), eq("user-1"), any(Instant.class), eq(fixedNow)))
                .thenAnswer(invocation -> {
                    slot.setStatus(SlotStatus.HELD);
                    slot.setHeldBy("user-1");
                    slot.setHeldUntil((Instant) invocation.getArgument(4));
                    return 1;
                });

        AvailabilitySlot heldSlot = service.holdSlot(100L, "user-1", Duration.ofMinutes(15));
        assertEquals(SlotStatus.HELD, heldSlot.getStatus());
        assertEquals("user-1", heldSlot.getHeldBy());

        when(slotRepository.bookSlotAtomically(eq(100L), eq(SlotStatus.HELD), eq(SlotStatus.BOOKED), eq("booking-99"), eq(fixedNow)))
                .thenAnswer(invocation -> {
                    slot.setStatus(SlotStatus.BOOKED);
                    slot.setBookingId("booking-99");
                    return 1;
                });

        AvailabilitySlot bookedSlot = service.bookSlot(100L, "booking-99", "user-1");
        assertEquals(SlotStatus.BOOKED, bookedSlot.getStatus());
        assertEquals("booking-99", bookedSlot.getBookingId());

        verify(historyRepository, times(1)).save(argThat(h -> h.getNewStatus() == SlotStatus.HELD));
        verify(historyRepository, times(1)).save(argThat(h -> h.getNewStatus() == SlotStatus.BOOKED));
    }

    @Test
    void testNoShowUpdateMaintainsHistoricalRecord() {
        Instant startTime = fixedNow.minus(Duration.ofHours(2));
        Instant endTime = fixedNow.minus(Duration.ofHours(1));

        AvailabilitySlot slot = new AvailabilitySlot("master-1", "service-a", startTime, endTime);
        slot.setId(200L);
        slot.setStatus(SlotStatus.BOOKED);
        slot.setBookingId("booking-88");

        when(slotRepository.findById(200L)).thenReturn(Optional.of(slot));
        when(slotRepository.updateSlotStatusAtomically(eq(200L), eq(SlotStatus.BOOKED), eq(SlotStatus.NO_SHOW), eq(fixedNow)))
                .thenAnswer(invocation -> {
                    slot.setStatus(SlotStatus.NO_SHOW);
                    return 1;
                });

        AvailabilitySlot noShowSlot = service.markNoShow(200L, "Customer did not arrive", "admin-1");

        assertEquals(SlotStatus.NO_SHOW, noShowSlot.getStatus());

        verify(historyRepository, times(1)).save(argThat(history ->
                history.getSlotId().equals(200L) &&
                history.getPreviousStatus() == SlotStatus.BOOKED &&
                history.getNewStatus() == SlotStatus.NO_SHOW &&
                history.getChangedBy().equals("admin-1") &&
                history.getReason().equals("Customer did not arrive") &&
                history.getChangedAt().equals(fixedNow)
        ));
    }

    @Test
    void testInvalidTransitionThrowsConflictException() {
        AvailabilitySlot slot = new AvailabilitySlot("master-1", "service-a", fixedNow, fixedNow.plus(Duration.ofHours(1)));
        slot.setId(300L);
        slot.setStatus(SlotStatus.BOOKED);

        when(slotRepository.findById(300L)).thenReturn(Optional.of(slot));

        assertThrows(SlotStateConflictException.class, () ->
                service.holdSlot(300L, "user-2", Duration.ofMinutes(10))
        );
    }
}
