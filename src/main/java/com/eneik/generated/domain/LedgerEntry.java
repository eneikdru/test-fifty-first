package com.eneik.generated.domain;

import java.math.BigDecimal;
import java.time.Instant;

public class LedgerEntry {
    private String bookingId;
    private String professionalId;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;

    public LedgerEntry() {
    }

    public LedgerEntry(String bookingId, String professionalId, BigDecimal amount, String currency, PaymentMethod paymentMethod, PaymentStatus status, String notes, Instant createdAt, Instant updatedAt) {
        this.bookingId = bookingId;
        this.professionalId = professionalId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(String professionalId) {
        this.professionalId = professionalId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LedgerEntry copy() {
        return new LedgerEntry(
                this.bookingId,
                this.professionalId,
                this.amount,
                this.currency,
                this.paymentMethod,
                this.status,
                this.notes,
                this.createdAt,
                this.updatedAt
        );
    }
}
