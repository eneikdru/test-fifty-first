package com.eneik.generated.messaging.service;

import com.eneik.generated.messaging.domain.*;
import com.eneik.generated.messaging.gateway.NotificationGateway;
import com.eneik.generated.messaging.repository.BookingRepository;
import com.eneik.generated.messaging.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ReminderSchedulerService {
    private final BookingRepository bookingRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationGateway notificationGateway;
    private final Clock clock;
    private final Duration reminderWindow;

    @Autowired
    public ReminderSchedulerService(BookingRepository bookingRepository,
                                   NotificationRepository notificationRepository,
                                   NotificationGateway notificationGateway,
                                   Clock clock) {
        this(bookingRepository, notificationRepository, notificationGateway, clock, Duration.ofHours(24));
    }

    public ReminderSchedulerService(BookingRepository bookingRepository,
                                   NotificationRepository notificationRepository,
                                   NotificationGateway notificationGateway,
                                   Clock clock,
                                   Duration reminderWindow) {
        this.bookingRepository = bookingRepository;
        this.notificationRepository = notificationRepository;
        this.notificationGateway = notificationGateway;
        this.clock = clock;
        this.reminderWindow = reminderWindow;
    }

    @Scheduled(fixedRate = 60000)
    public List<Notification> runReminderJob() {
        Instant now = Instant.now(clock);
        Instant windowEnd = now.plus(reminderWindow);

        List<Booking> impendingBookings = bookingRepository.findUpcomingBookings(now, windowEnd);
        List<Notification> dispatchedReminders = new ArrayList<>();

        for (Booking booking : impendingBookings) {
            // Check if reminder notification already exists for this booking
            List<Notification> existingReminders = notificationRepository.findByBookingIdAndType(
                    booking.getId(), NotificationType.REMINDER
            );
            if (!existingReminders.isEmpty()) {
                continue;
            }

            NotificationChannel channel = booking.getCustomerPhone() != null && !booking.getCustomerPhone().isBlank()
                    ? NotificationChannel.SMS
                    : NotificationChannel.EMAIL;
            String recipient = channel == NotificationChannel.SMS ? booking.getCustomerPhone() : booking.getCustomerEmail();

            Notification reminder = new Notification(
                    UUID.randomUUID().toString(),
                    booking.getId(),
                    recipient,
                    channel,
                    NotificationType.REMINDER,
                    "Reminder: Upcoming appointment for " + booking.getServiceName() + " at " + booking.getAppointmentTime(),
                    NotificationStatus.QUEUED,
                    now,
                    null
            );

            notificationRepository.save(reminder);

            boolean sentSuccess = notificationGateway.send(reminder);
            if (sentSuccess) {
                if (notificationRepository.updateStatusAtomically(reminder.getId(), NotificationStatus.QUEUED, NotificationStatus.SENT)) {
                    reminder.setSentAt(now);
                    dispatchedReminders.add(reminder);
                }
            } else {
                notificationRepository.updateStatusAtomically(reminder.getId(), NotificationStatus.QUEUED, NotificationStatus.FAILED);
            }
        }

        return dispatchedReminders;
    }
}
