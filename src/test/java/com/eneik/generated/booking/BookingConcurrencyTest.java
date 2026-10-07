package com.eneik.generated.booking;

import com.eneik.generated.model.AvailabilitySlot;
import com.eneik.generated.model.BookingEntity;
import com.eneik.generated.model.MasterProfile;
import com.eneik.generated.repository.AvailabilitySlotRepository;
import com.eneik.generated.repository.BookingRepository;
import com.eneik.generated.repository.MasterProfileRepository;
import com.eneik.generated.service.BookingEngineService;
import com.eneik.generated.service.BookingEngineService.SlotUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class BookingConcurrencyTest {

    @Autowired
    private BookingEngineService bookingEngineService;

    @Autowired
    private MasterProfileRepository masterRepository;

    @Autowired
    private AvailabilitySlotRepository slotRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private Long masterId;
    private Long slotId;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();

        List<MasterProfile> masters = masterRepository.findAll();
        MasterProfile master;
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        if (masters.isEmpty()) {
            master = masterRepository.save(new MasterProfile("Test Master", "Tbilisi", "+995599000111", null, "Master description", now));
        } else {
            master = masters.get(0);
        }
        this.masterId = master.getId();

        AvailabilitySlot slot = new AvailabilitySlot(masterId, now.plusHours(10), now.plusHours(11), "AVAILABLE", now);
        slot = slotRepository.save(slot);
        this.slotId = slot.getId();
    }

    @Test
    @DisplayName("Given two simultaneous hold requests for the same slot, exactly one succeeds")
    void testSimultaneousHoldRequestsConcurrency() throws Exception {
        int threads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<Exception> exceptions = new CopyOnWriteArrayList<>();

        for (int i = 0; i < threads; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    BookingEntity booking = bookingEngineService.createHold(
                            slotId,
                            masterId,
                            "+99559900011" + threadNum,
                            "Haircut",
                            50.0
                    );
                    if (booking != null && "HELD".equals(booking.getStatus())) {
                        successCount.incrementAndGet();
                    }
                } catch (SlotUnavailableException e) {
                    failureCount.incrementAndGet();
                    exceptions.add(e);
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        readyLatch.await(5, TimeUnit.SECONDS);
        startLatch.countDown();
        boolean completed = finishLatch.await(5, TimeUnit.SECONDS);

        executor.shutdown();

        assertTrue(completed, "All threads should complete within timeout");
        assertEquals(1, successCount.get(), "Exactly one hold request must succeed");
        assertEquals(1, failureCount.get(), "Exactly one hold request must fail with SlotUnavailableException");

        AvailabilitySlot updatedSlot = slotRepository.findById(slotId).orElseThrow();
        assertEquals("HELD", updatedSlot.getStatus(), "Slot status must be updated to HELD");

        List<BookingEntity> bookings = bookingRepository.findBySlotId(slotId);
        assertEquals(1, bookings.size(), "Only one booking record must be persisted for the slot");
    }
}
