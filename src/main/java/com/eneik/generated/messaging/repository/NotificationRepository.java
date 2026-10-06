package com.eneik.generated.messaging.repository;

import com.eneik.generated.messaging.domain.Notification;
import com.eneik.generated.messaging.domain.NotificationStatus;
import com.eneik.generated.messaging.domain.NotificationType;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Repository
public class NotificationRepository {
    private final ConcurrentHashMap<String, Notification> storage = new ConcurrentHashMap<>();

    public Notification save(Notification notification) {
        storage.put(notification.getId(), notification);
        return notification;
    }

    public Optional<Notification> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Notification> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Notification> findByBookingId(String bookingId) {
        List<Notification> result = new ArrayList<>();
        for (Notification notification : storage.values()) {
            if (notification.getBookingId().equals(bookingId)) {
                result.add(notification);
            }
        }
        return result;
    }

    public List<Notification> findByBookingIdAndType(String bookingId, NotificationType type) {
        List<Notification> result = new ArrayList<>();
        for (Notification notification : storage.values()) {
            if (notification.getBookingId().equals(bookingId) && notification.getType() == type) {
                result.add(notification);
            }
        }
        return result;
    }

    /**
     * Atomically updates status from expectedStatus to newStatus.
     * Guarantees atomic transition guard.
     */
    public boolean updateStatusAtomically(String notificationId, NotificationStatus expectedStatus, NotificationStatus newStatus) {
        AtomicBoolean updated = new AtomicBoolean(false);
        storage.computeIfPresent(notificationId, (id, current) -> {
            if (current.getStatus() == expectedStatus) {
                current.setStatus(newStatus);
                updated.set(true);
            }
            return current;
        });
        return updated.get();
    }
}
