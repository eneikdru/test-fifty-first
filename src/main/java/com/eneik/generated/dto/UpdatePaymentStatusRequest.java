package com.eneik.generated.dto;

import com.eneik.generated.domain.PaymentStatus;

public class UpdatePaymentStatusRequest {
    private PaymentStatus expectedStatus;
    private PaymentStatus newStatus;
    private String notes;

    public UpdatePaymentStatusRequest() {
    }

    public PaymentStatus getExpectedStatus() {
        return expectedStatus;
    }

    public void setExpectedStatus(PaymentStatus expectedStatus) {
        this.expectedStatus = expectedStatus;
    }

    public PaymentStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(PaymentStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
