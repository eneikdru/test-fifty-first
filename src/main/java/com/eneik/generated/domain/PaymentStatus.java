package com.eneik.generated.domain;

public enum PaymentStatus {
    PENDING,
    MANUALLY_VERIFIED,
    SETTLED,
    CANCELLED;

    public boolean isTerminal() {
        return this == SETTLED || this == CANCELLED;
    }
}
