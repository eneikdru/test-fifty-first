package com.eneik.generated.privacy.model;

import com.eneik.generated.privacy.dto.BookingExportItem;
import com.eneik.generated.privacy.dto.NotificationLogExportItem;
import com.eneik.generated.privacy.dto.VoiceBookingExportItem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class UserDataRecord {
    private String userId;
    private String phone;
    private String facebookId;
    private String fullName;
    private String city;
    private Instant createdAt;

    private String masterId;
    private String facebookPageId;
    private String publicProfileUrl;
    private String description;
    private String address;
    private List<String> importedPhotos = new ArrayList<>();

    private List<BookingExportItem> bookings = new ArrayList<>();
    private List<VoiceBookingExportItem> voiceRequests = new ArrayList<>();
    private List<NotificationLogExportItem> messengerNotifications = new ArrayList<>();

    private boolean erased = false;

    public UserDataRecord() {}

    public UserDataRecord(String userId, String phone, String facebookId, String fullName, String city, Instant createdAt) {
        this.userId = userId;
        this.phone = phone;
        this.facebookId = facebookId;
        this.fullName = fullName;
        this.city = city;
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public String getFacebookPageId() {
        return facebookPageId;
    }

    public void setFacebookPageId(String facebookPageId) {
        this.facebookPageId = facebookPageId;
    }

    public String getPublicProfileUrl() {
        return publicProfileUrl;
    }

    public void setPublicProfileUrl(String publicProfileUrl) {
        this.publicProfileUrl = publicProfileUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getImportedPhotos() {
        return importedPhotos;
    }

    public void setImportedPhotos(List<String> importedPhotos) {
        this.importedPhotos = importedPhotos;
    }

    public List<BookingExportItem> getBookings() {
        return bookings;
    }

    public void setBookings(List<BookingExportItem> bookings) {
        this.bookings = bookings;
    }

    public List<VoiceBookingExportItem> getVoiceRequests() {
        return voiceRequests;
    }

    public void setVoiceRequests(List<VoiceBookingExportItem> voiceRequests) {
        this.voiceRequests = voiceRequests;
    }

    public List<NotificationLogExportItem> getMessengerNotifications() {
        return messengerNotifications;
    }

    public void setMessengerNotifications(List<NotificationLogExportItem> messengerNotifications) {
        this.messengerNotifications = messengerNotifications;
    }

    public boolean isErased() {
        return erased;
    }

    public void setErased(boolean erased) {
        this.erased = erased;
    }
}
