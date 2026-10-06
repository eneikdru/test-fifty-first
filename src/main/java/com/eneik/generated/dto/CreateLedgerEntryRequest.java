package com.eneik.generated.dto;

import com.eneik.generated.domain.PaymentMethod;

import java.math.BigDecimal;

public class CreateLedgerEntryRequest {
    private String professionalId;
    private BigDecimal amount;
    private String currency = "GEL";
    private PaymentMethod paymentMethod;
    private String notes;

    public CreateLedgerEntryRequest() {
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
