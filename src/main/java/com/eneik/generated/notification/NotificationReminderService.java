package com.eneik.generated.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationReminderService {

    private final SmsSender smsSender;
    private final Clock clock;
    private final Map<String, Booking> bookingStore = new ConcurrentHashMap<>();

    @Autowired
    public NotificationReminderService(SmsSender smsSender, Clock clock) {
        this.smsSender = smsSender;
        this.clock = clock;
    }

    public NotificationReminderService(SmsSender smsSender) {
        this(smsSender, Clock.systemUTC());
    }

    public void saveBooking(Booking booking) {
        bookingStore.put(booking.getBookingId(), booking);
    }

    public Optional<Booking> getBooking(String bookingId) {
        return Optional.ofNullable(bookingStore.get(bookingId));
    }

    public void triggerBookingReminder(String bookingId) {
        Booking booking = bookingStore.get(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }
        triggerBookingReminder(booking);
    }

    public void triggerBookingReminder(Booking booking) {
        String content = String.format("Reminder: Appointment for %s with %s is scheduled. Price: %.2f GEL.",
                booking.getServiceName(), booking.getMasterName(), booking.getPriceGel());
        smsSender.sendSms(booking.getCustomerPhone(), content);
    }

    public boolean processMessengerAction(String bookingId, String action) {
        Booking booking = bookingStore.get(bookingId);
        if (booking == null) {
            return false;
        }

        if ("CONFIRM".equalsIgnoreCase(action)) {
            // Atomic compare-and-swap from PENDING to CONFIRMED
            return booking.compareAndSetStatus(BookingStatus.PENDING, BookingStatus.CONFIRMED);
        } else if ("CANCEL".equalsIgnoreCase(action)) {
            // Atomic compare-and-swap from PENDING to CANCELLED
            return booking.compareAndSetStatus(BookingStatus.PENDING, BookingStatus.CANCELLED);
        }

        return false;
    }
}
