package com.eneik.generated.notification;

import java.time.Instant;

public record SmsMessage(
    String recipientPhone,
    String messageContent,
    Instant sentAt
) {}
