package com.eneik.booking.ai.model;

public class BookingIntentRequest {
    private String audioDataBase64;
    private String textInput;
    private String language = "ka";

    public BookingIntentRequest() {
    }

    public BookingIntentRequest(String textInput) {
        this.textInput = textInput;
    }

    public BookingIntentRequest(String audioDataBase64, String textInput, String language) {
        this.audioDataBase64 = audioDataBase64;
        this.textInput = textInput;
        this.language = language != null ? language : "ka";
    }

    public String getAudioDataBase64() {
        return audioDataBase64;
    }

    public void setAudioDataBase64(String audioDataBase64) {
        this.audioDataBase64 = audioDataBase64;
    }

    public String getTextInput() {
        return textInput;
    }

    public void setTextInput(String textInput) {
        this.textInput = textInput;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
