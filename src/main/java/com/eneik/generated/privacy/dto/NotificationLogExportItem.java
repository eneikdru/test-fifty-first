package com.eneik.generated.privacy.dto;

import java.time.Instant;

public record NotificationLogExportItem(
        String channel,
        String messageType,
        Instant sentAt
) {}
