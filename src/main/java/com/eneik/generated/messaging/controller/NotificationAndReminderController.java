package com.eneik.generated.messaging.controller;

import com.eneik.generated.messaging.domain.Notification;
import com.eneik.generated.messaging.repository.NotificationRepository;
import com.eneik.generated.messaging.service.ReminderSchedulerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class NotificationAndReminderController {
    private final NotificationRepository notificationRepository;
    private final ReminderSchedulerService reminderSchedulerService;

    public NotificationAndReminderController(NotificationRepository notificationRepository,
                                             ReminderSchedulerService reminderSchedulerService) {
        this.notificationRepository = notificationRepository;
        this.reminderSchedulerService = reminderSchedulerService;
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getAllNotifications(
            @RequestParam(required = false) String bookingId) {
        if (bookingId != null && !bookingId.isBlank()) {
            return ResponseEntity.ok(notificationRepository.findByBookingId(bookingId));
        }
        return ResponseEntity.ok(notificationRepository.findAll());
    }

    @PostMapping("/reminders/trigger")
    public ResponseEntity<List<Notification>> triggerReminders() {
        List<Notification> dispatched = reminderSchedulerService.runReminderJob();
        return ResponseEntity.ok(dispatched);
    }
}
