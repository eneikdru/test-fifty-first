package com.eneik.generated.privacy.dto;

import java.time.Instant;
import java.util.List;

public record PrivacyExportRequest(
        String format,
        Boolean includeMedia,
        String callbackUrl
) {
    public PrivacyExportRequest {
        if (format == null) {
            format = "JSON";
        }
        if (includeMedia == null) {
            includeMedia = true;
        }
    }
}
