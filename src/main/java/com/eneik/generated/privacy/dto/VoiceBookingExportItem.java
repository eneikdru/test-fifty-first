package com.eneik.generated.privacy.dto;

import java.time.Instant;

public record VoiceBookingExportItem(
        String requestId,
        String transcribedText,
        Float audioDurationSeconds,
        Instant createdAt
) {}
