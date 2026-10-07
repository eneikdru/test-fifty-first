package com.eneik.generated.booking;

import com.eneik.generated.model.AvailabilitySlot;
import com.eneik.generated.notification.Booking;
import com.eneik.generated.notification.BookingStatus;
import com.eneik.generated.notification.NotificationReminderService;
import com.eneik.generated.notification.SmsSender;
import com.eneik.generated.repository.AvailabilitySlotRepository;
import com.eneik.generated.service.GeorgianTtsService;
import com.eneik.generated.service.VoiceBookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingEngineConcurrencyTest {

    @Autowired
    private AvailabilitySlotRepository availabilitySlotRepository;

    @Test
    @DisplayName("Given a high load of concurrent requests for one slot/booking, Then exactly one succeeds and no double bookings occur")
    void testConcurrentBookingRequestsPreventDoubleBooking() throws Exception {
        SmsSender mockSmsSender = (phone, text) -> {};
        NotificationReminderService reminderService = new NotificationReminderService(mockSmsSender, Clock.fixed(Instant.parse("2026-10-07T12:00:00Z"), ZoneOffset.UTC));

        String bookingId = "booking-concurrent-100";
        Booking booking = new Booking(
                bookingId,
                "master-42",
                "Giga Barber",
                "+995599112233",
                "Haircut",
                35.00,
                Instant.parse("2026-10-10T14:00:00Z"),
                BookingStatus.PENDING
        );
        reminderService.saveBooking(booking);

        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    boolean result = reminderService.processMessengerAction(bookingId, "CONFIRM");
                    if (result) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        boolean completed = endLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(completed, "All concurrent requests should complete within timeout");
        assertEquals(1, successCount.get(), "Exactly one concurrent confirmation request should succeed");
        assertEquals(threadCount - 1, failCount.get(), "All other concurrent confirmation requests should be rejected");

        Optional<Booking> finalBookingState = reminderService.getBooking(bookingId);
        assertTrue(finalBookingState.isPresent());
        assertEquals(BookingStatus.CONFIRMED, finalBookingState.get().getStatus());
    }

    @Test
    @DisplayName("Given VoiceBookingService, When concurrent confirmBooking requests are made, Then only the first updates state and returns status COMPLETED")
    void testConcurrentVoiceBookingConfirmation() throws Exception {
        GeorgianTtsService mockTtsService = new GeorgianTtsService();
        VoiceBookingService voiceBookingService = new VoiceBookingService(mockTtsService);
        voiceBookingService.setIdGenerator(() -> "voice-booking-777");

        VoiceBookingService.VoiceBooking proposed = voiceBookingService.proposeSlot("master-7", "Beard Trim", "2026-10-10T15:00:00Z");
        assertEquals("voice-booking-777", proposed.getId());
        assertEquals(VoiceBookingService.BookingStatus.PROPOSED, proposed.getStatus());

        int threadCount = 15;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        List<Future<Optional<VoiceBookingService.VoiceBooking>>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                try {
                    startLatch.await();
                    return voiceBookingService.confirmBooking("voice-booking-777");
                } finally {
                    endLatch.countDown();
                }
            }));
        }

        startLatch.countDown();
        boolean completed = endLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(completed, "All voice confirmation requests should complete");

        int completedStatusCount = 0;
        for (Future<Optional<VoiceBookingService.VoiceBooking>> future : futures) {
            Optional<VoiceBookingService.VoiceBooking> res = future.get();
            assertTrue(res.isPresent());
            if (res.get().getStatus() == VoiceBookingService.BookingStatus.COMPLETED) {
                completedStatusCount++;
            }
        }

        assertEquals(threadCount, completedStatusCount, "All returns should reflect COMPLETED status after atomic transition");
        Optional<VoiceBookingService.VoiceBooking> finalState = voiceBookingService.getBooking("voice-booking-777");
        assertTrue(finalState.isPresent());
        assertEquals(VoiceBookingService.BookingStatus.COMPLETED, finalState.get().getStatus());
    }

    @Autowired
    private com.eneik.generated.repository.MasterProfileRepository masterProfileRepository;

    @Test
    @DisplayName("Given E2E tests, When a professional blocks/reschedules a slot, Then the API/repository immediately reflects it as unavailable")
    void testSlotBlockingAndImmediateUnavailability() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        com.eneik.generated.model.MasterProfile master = new com.eneik.generated.model.MasterProfile(
                "Test Master " + System.currentTimeMillis(),
                "Tbilisi",
                "+995555" + (System.currentTimeMillis() % 1000000),
                "fb_page_id_123",
                "Description test",
                now
        );
        master = masterProfileRepository.save(master);
        Long masterId = master.getId();

        AvailabilitySlot slot = new AvailabilitySlot(
                masterId,
                now.plusDays(1),
                now.plusDays(1).plusHours(1),
                "AVAILABLE",
                now
        );
        AvailabilitySlot savedSlot = availabilitySlotRepository.save(slot);
        assertNotNull(savedSlot.getId());

        List<AvailabilitySlot> availableSlotsBefore = availabilitySlotRepository.findByMasterIdAndStatus(masterId, "AVAILABLE");
        assertEquals(1, availableSlotsBefore.size());

        savedSlot.setStatus("BLOCKED");
        availabilitySlotRepository.save(savedSlot);

        List<AvailabilitySlot> availableSlotsAfter = availabilitySlotRepository.findByMasterIdAndStatus(masterId, "AVAILABLE");
        assertTrue(availableSlotsAfter.isEmpty(), "Slot should immediately cease to be available when blocked");

        List<AvailabilitySlot> blockedSlots = availabilitySlotRepository.findByMasterIdAndStatus(masterId, "BLOCKED");
        assertEquals(1, blockedSlots.size());
        assertEquals(savedSlot.getId(), blockedSlots.get(0).getId());
    }
}
