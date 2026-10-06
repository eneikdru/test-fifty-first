package com.eneik.generated.privacy.dto;

public record PrivacyErasureRequest(
        Boolean confirmErasure,
        String erasureScope,
        String reason
) {
    public PrivacyErasureRequest {
        if (erasureScope == null) {
            erasureScope = "ALL_DATA";
        }
    }
}
