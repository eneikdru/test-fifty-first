package com.eneik.generated.messaging.service;

import com.eneik.generated.messaging.domain.*;
import com.eneik.generated.messaging.gateway.NotificationGateway;
import com.eneik.generated.messaging.repository.BookingRepository;
import com.eneik.generated.messaging.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationGateway notificationGateway;
    private final Clock clock;

    public BookingService(BookingRepository bookingRepository,
                          NotificationRepository notificationRepository,
                          NotificationGateway notificationGateway,
                          Clock clock) {
        this.bookingRepository = bookingRepository;
        this.notificationRepository = notificationRepository;
        this.notificationGateway = notificationGateway;
        this.clock = clock;
    }

    public Booking createBooking(String customerId, String customerPhone, String customerEmail,
                                 String masterId, String serviceName, Instant appointmentTime) {
        String bookingId = UUID.randomUUID().toString();
        Instant now = Instant.now(clock);

        Booking booking = new Booking(
                bookingId,
                customerId,
                customerPhone,
                customerEmail,
                masterId,
                serviceName,
                appointmentTime,
                BookingStatus.CONFIRMED,
                now
        );
        bookingRepository.save(booking);

        // Queue automatic acknowledgement for customer
        NotificationChannel channel = customerPhone != null && !customerPhone.isBlank()
                ? NotificationChannel.SMS
                : NotificationChannel.EMAIL;
        String recipient = channel == NotificationChannel.SMS ? customerPhone : customerEmail;

        Notification acknowledgement = new Notification(
                UUID.randomUUID().toString(),
                bookingId,
                recipient,
                channel,
                NotificationType.ACKNOWLEDGEMENT,
                "Booking confirmed for service " + serviceName + " at " + appointmentTime,
                NotificationStatus.QUEUED,
                now,
                null
        );

        notificationRepository.save(acknowledgement);

        // Dispatch notification and update status accordingly
        boolean sentSuccess = notificationGateway.send(acknowledgement);
        if (sentSuccess) {
            if (notificationRepository.updateStatusAtomically(acknowledgement.getId(), NotificationStatus.QUEUED, NotificationStatus.SENT)) {
                acknowledgement.setSentAt(now);
            }
        } else {
            notificationRepository.updateStatusAtomically(acknowledgement.getId(), NotificationStatus.QUEUED, NotificationStatus.FAILED);
        }

        return booking;
    }

    public BookingRepository getBookingRepository() {
        return bookingRepository;
    }

    public NotificationRepository getNotificationRepository() {
        return notificationRepository;
    }
}
