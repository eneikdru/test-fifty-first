package com.eneik.generated.privacy.dto;

import java.time.Instant;

public record UserProfileExport(
        String id,
        String phone,
        String facebookId,
        String fullName,
        String city,
        Instant createdAt
) {}
