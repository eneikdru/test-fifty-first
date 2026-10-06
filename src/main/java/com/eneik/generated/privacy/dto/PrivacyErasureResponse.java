package com.eneik.generated.privacy.dto;

import java.time.Instant;

public record PrivacyErasureResponse(
        String erasureRequestId,
        String userId,
        String status,
        Instant requestedAt,
        Instant scheduledErasureTime,
        Instant completedAt,
        String confirmationCode
) {}
