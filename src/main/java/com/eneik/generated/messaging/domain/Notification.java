package com.eneik.generated.messaging.domain;

import java.time.Instant;

public class Notification {
    private final String id;
    private final String bookingId;
    private final String recipient;
    private final NotificationChannel channel;
    private final NotificationType type;
    private final String message;
    private NotificationStatus status;
    private final Instant scheduledAt;
    private Instant sentAt;

    public Notification(String id, String bookingId, String recipient, NotificationChannel channel,
                        NotificationType type, String message, NotificationStatus status,
                        Instant scheduledAt, Instant sentAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.recipient = recipient;
        this.channel = channel;
        this.type = type;
        this.message = message;
        this.status = status;
        this.scheduledAt = scheduledAt;
        this.sentAt = sentAt;
    }

    public String getId() { return id; }
    public String getBookingId() { return bookingId; }
    public String getRecipient() { return recipient; }
    public NotificationChannel getChannel() { return channel; }
    public NotificationType getType() { return type; }
    public String getMessage() { return message; }
    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { this.status = status; }
    public Instant getScheduledAt() { return scheduledAt; }
    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
}
