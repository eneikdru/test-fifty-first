package com.eneik.generated.dto;

public class PaymentInstructionsRequest {
    private String accountHolderName;
    private String tbcIban;
    private String bogIban;
    private Boolean acceptsCash = true;
    private String instructionsNote;

    public PaymentInstructionsRequest() {
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

    public Boolean getAcceptsCash() {
        return acceptsCash;
    }

    public void setAcceptsCash(Boolean acceptsCash) {
        this.acceptsCash = acceptsCash;
    }

    public String getInstructionsNote() {
        return instructionsNote;
    }

    public void setInstructionsNote(String instructionsNote) {
        this.instructionsNote = instructionsNote;
    }
}
