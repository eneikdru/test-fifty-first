package com.eneik.booking.ai.model;

import java.time.LocalDateTime;

public class BookingIntentResponse {
    private String transcribedText;
    private String extractedService;
    private LocalDateTime requestedTime;
    private String location;
    private double confidenceScore;
    private boolean lowConfidence;
    private String status;

    public BookingIntentResponse() {
    }

    public BookingIntentResponse(String transcribedText, String extractedService, LocalDateTime requestedTime,
                                 String location, double confidenceScore, boolean lowConfidence, String status) {
        this.transcribedText = transcribedText;
        this.extractedService = extractedService;
        this.requestedTime = requestedTime;
        this.location = location;
        this.confidenceScore = confidenceScore;
        this.lowConfidence = lowConfidence;
        this.status = status;
    }

    public String getTranscribedText() {
        return transcribedText;
    }

    public void setTranscribedText(String transcribedText) {
        this.transcribedText = transcribedText;
    }

    public String getExtractedService() {
        return extractedService;
    }

    public void setExtractedService(String extractedService) {
        this.extractedService = extractedService;
    }

    public LocalDateTime getRequestedTime() {
        return requestedTime;
    }

    public void setRequestedTime(LocalDateTime requestedTime) {
        this.requestedTime = requestedTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public boolean isLowConfidence() {
        return lowConfidence;
    }

    public void setLowConfidence(boolean lowConfidence) {
        this.lowConfidence = lowConfidence;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
