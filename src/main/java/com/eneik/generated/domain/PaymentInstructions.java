package com.eneik.generated.domain;

import java.time.Instant;

public class PaymentInstructions {
    private String professionalId;
    private String accountHolderName;
    private String tbcIban;
    private String bogIban;
    private boolean acceptsCash;
    private String instructionsNote;
    private Instant updatedAt;

    public PaymentInstructions() {
    }

    public PaymentInstructions(String professionalId, String accountHolderName, String tbcIban, String bogIban, boolean acceptsCash, String instructionsNote, Instant updatedAt) {
        this.professionalId = professionalId;
        this.accountHolderName = accountHolderName;
        this.tbcIban = tbcIban;
        this.bogIban = bogIban;
        this.acceptsCash = acceptsCash;
        this.instructionsNote = instructionsNote;
        this.updatedAt = updatedAt;
    }

    public String getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(String professionalId) {
        this.professionalId = professionalId;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getTbcIban() {
        return tbcIban;
    }

    public void setTbcIban(String tbcIban) {
        this.tbcIban = tbcIban;
    }

    public String getBogIban() {
        return bogIban;
    }

    public void setBogIban(String bogIban) {
        this.bogIban = bogIban;
    }

    public boolean isAcceptsCash() {
        return acceptsCash;
    }

    public void setAcceptsCash(boolean acceptsCash) {
        this.acceptsCash = acceptsCash;
    }

    public String getInstructionsNote() {
        return instructionsNote;
    }

    public void setInstructionsNote(String instructionsNote) {
        this.instructionsNote = instructionsNote;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
