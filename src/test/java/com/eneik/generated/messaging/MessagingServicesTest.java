package com.eneik.generated.messaging;

import com.eneik.generated.messaging.domain.*;
import com.eneik.generated.messaging.gateway.InMemoryNotificationGateway;
import com.eneik.generated.messaging.repository.BookingRepository;
import com.eneik.generated.messaging.repository.NotificationRepository;
import com.eneik.generated.messaging.service.BookingService;
import com.eneik.generated.messaging.service.ReminderSchedulerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessagingServicesTest {

    private BookingRepository bookingRepository;
    private NotificationRepository notificationRepository;
    private InMemoryNotificationGateway notificationGateway;
    private Clock fixedClock;
    private Instant now;

    @BeforeEach
    void setUp() {
        bookingRepository = new BookingRepository();
        notificationRepository = new NotificationRepository();
        notificationGateway = new InMemoryNotificationGateway();
        now = Instant.parse("2026-10-06T12:00:00Z");
        fixedClock = Clock.fixed(now, ZoneId.of("UTC"));
    }

    @Test
    void whenBookingCreated_thenAcknowledgementIsQueuedAndDispatched() {
        BookingService bookingService = new BookingService(
                bookingRepository, notificationRepository, notificationGateway, fixedClock
        );

        Instant appointmentTime = now.plus(Duration.ofHours(5));
        Booking booking = bookingService.createBooking(
                "cust-123",
                "+995555123456",
                "customer@example.com",
                "master-789",
                "Haircut",
                appointmentTime
        );

        assertNotNull(booking);
        assertNotNull(booking.getId());
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());

        List<Notification> notifications = notificationRepository.findByBookingId(booking.getId());
        assertEquals(1, notifications.size());

        Notification acknowledgement = notifications.get(0);
        assertEquals(NotificationType.ACKNOWLEDGEMENT, acknowledgement.getType());
        assertEquals(NotificationStatus.SENT, acknowledgement.getStatus());
        assertEquals("+995555123456", acknowledgement.getRecipient());

        assertEquals(1, notificationGateway.getDispatchedNotifications().size());
    }

    @Test
    void whenImpendingAppointment_thenReminderSchedulerDispatchesReminder() {
        BookingService bookingService = new BookingService(
                bookingRepository, notificationRepository, notificationGateway, fixedClock
        );
        ReminderSchedulerService reminderSchedulerService = new ReminderSchedulerService(
                bookingRepository, notificationRepository, notificationGateway, fixedClock, Duration.ofHours(24)
        );

        Instant appointmentTime = now.plus(Duration.ofHours(12));
        Booking booking = bookingService.createBooking(
                "cust-123",
                "+995555123456",
                "customer@example.com",
                "master-789",
                "Beard Trim",
                appointmentTime
        );

        // Run reminder scheduler job
        List<Notification> dispatchedReminders = reminderSchedulerService.runReminderJob();

        assertEquals(1, dispatchedReminders.size());
        Notification reminder = dispatchedReminders.get(0);
        assertEquals(booking.getId(), reminder.getBookingId());
        assertEquals(NotificationType.REMINDER, reminder.getType());
        assertEquals(NotificationStatus.SENT, reminder.getStatus());

        // Subsequent run should not duplicate reminder
        List<Notification> secondRunReminders = reminderSchedulerService.runReminderJob();
        assertTrue(secondRunReminders.isEmpty());
    }
}
