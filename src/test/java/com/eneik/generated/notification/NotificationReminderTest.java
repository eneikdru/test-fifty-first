package com.eneik.generated.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationReminderTest {

    private final Instant fixedInstant = Instant.parse("2026-10-06T12:00:00Z");
    private final Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));

    private TestSmsSender testSmsSender;
    private NotificationReminderService reminderService;

    @BeforeEach
    void setUp() {
        testSmsSender = new TestSmsSender(fixedClock);
        reminderService = new NotificationReminderService(testSmsSender, fixedClock);
    }

    @Test
    @DisplayName("Given a triggered reminder, When checked in the test double, Then the SMS content is verified")
    void testTriggeredReminderSmsContentVerification() {
        String bookingId = "book-101";
        Booking booking = new Booking(
                bookingId,
                "master-01",
                "Giorgi Beridze",
                "+995555123456",
                "Haircut & Beard Trim",
                45.00,
                fixedInstant.plusSeconds(3600),
                BookingStatus.PENDING
        );
        reminderService.saveBooking(booking);

        // Trigger reminder
        reminderService.triggerBookingReminder(bookingId);

        // Check test double
        List<SmsMessage> sentMessages = testSmsSender.getSentMessages();
        assertEquals(1, sentMessages.size(), "Exactly one SMS should be sent");

        SmsMessage message = sentMessages.get(0);
        assertEquals("+995555123456", message.recipientPhone());
        assertEquals(fixedInstant, message.sentAt());
        assertTrue(message.messageContent().contains("Haircut & Beard Trim"), "SMS must contain service name");
        assertTrue(message.messageContent().contains("Giorgi Beridze"), "SMS must contain master name");
        assertTrue(message.messageContent().contains("45.00 GEL"), "SMS must contain price in GEL");
    }

    @Test
    @DisplayName("Given a messenger button click simulation, When processed, Then the booking state updates to confirmed")
    void testMessengerButtonClickUpdatesBookingStateToConfirmed() {
        String bookingId = "book-202";
        Booking booking = new Booking(
                bookingId,
                "master-02",
                "Nino Kvaratskhelia",
                "+995599887766",
                "Manicure",
                35.00,
                fixedInstant.plusSeconds(7200),
                BookingStatus.PENDING
        );
        reminderService.saveBooking(booking);

        // Simulate messenger button click (CONFIRM)
        boolean processed = reminderService.processMessengerAction(bookingId, "CONFIRM");

        assertTrue(processed, "Messenger action processing should succeed");
        Booking updatedBooking = reminderService.getBooking(bookingId).orElseThrow();
        assertEquals(BookingStatus.CONFIRMED, updatedBooking.getStatus(), "Booking state must be updated to CONFIRMED");
    }

    @Test
    @DisplayName("Verify atomic compare-and-swap prevents duplicate status transitions")
    void testAtomicStatusTransitionPreventsDuplicateConfirmations() {
        String bookingId = "book-303";
        Booking booking = new Booking(
                bookingId,
                "master-03",
                "David Maisuradze",
                "+995577112233",
                "Massage",
                80.00,
                fixedInstant.plusSeconds(10800),
                BookingStatus.PENDING
        );
        reminderService.saveBooking(booking);

        // First click succeeds
        boolean firstClick = reminderService.processMessengerAction(bookingId, "CONFIRM");
        assertTrue(firstClick, "First confirmation should succeed");

        // Second click fails because status is no longer PENDING
        boolean secondClick = reminderService.processMessengerAction(bookingId, "CONFIRM");
        assertFalse(secondClick, "Second confirmation attempt must be rejected as status is already CONFIRMED");
        assertEquals(BookingStatus.CONFIRMED, reminderService.getBooking(bookingId).get().getStatus());
    }

    @Test
    @DisplayName("Given a messenger CANCEL button click, booking state updates to CANCELLED")
    void testMessengerCancelButtonUpdatesBookingStateToCancelled() {
        String bookingId = "book-404";
        Booking booking = new Booking(
                bookingId,
                "master-04",
                "Ana Tsereteli",
                "+995591998877",
                "Coloring",
                120.00,
                fixedInstant.plusSeconds(14400),
                BookingStatus.PENDING
        );
        reminderService.saveBooking(booking);

        boolean processed = reminderService.processMessengerAction(bookingId, "CANCEL");
        assertTrue(processed, "Cancel action should succeed");
        assertEquals(BookingStatus.CANCELLED, reminderService.getBooking(bookingId).get().getStatus());
    }
}
