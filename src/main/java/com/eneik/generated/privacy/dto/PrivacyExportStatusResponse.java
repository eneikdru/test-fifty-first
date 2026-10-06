package com.eneik.generated.privacy.dto;

import java.time.Instant;

public record PrivacyExportStatusResponse(
        String exportId,
        String userId,
        String status,
        Instant requestedAt,
        Instant completedAt,
        String downloadUrl,
        PrivacyExportPayload payload
) {}
