package com.eneik.generated.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "master_profiles")
public class MasterProfile {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "full_name", nullable = false, length = 128)
    private String fullName;

    @Column(name = "city", nullable = false, length = 64)
    private String city;

    @Column(name = "phone_number", nullable = false, length = 32)
    private String phoneNumber;

    @Column(name = "facebook_profile_url", length = 256)
    private String facebookProfileUrl;

    @Column(name = "bio")
    private String bio;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public MasterProfile() {
    }

    public MasterProfile(String id, String fullName, String city, String phoneNumber, String facebookProfileUrl, String bio, OffsetDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.facebookProfileUrl = facebookProfileUrl;
        this.bio = bio;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFacebookProfileUrl() {
        return facebookProfileUrl;
    }

    public void setFacebookProfileUrl(String facebookProfileUrl) {
        this.facebookProfileUrl = facebookProfileUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MasterProfile that = (MasterProfile) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
