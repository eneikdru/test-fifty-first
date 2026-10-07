package com.eneik.generated.dto;

import com.eneik.generated.model.MasterProfile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MasterProfileResponse {
    private Long id;
    private String name;
    private String city;
    private String phone;
    private String facebookPageId;
    private String description;
    private String address;
    private List<String> photos;
    private String createdAt;

    public MasterProfileResponse() {
    }

    public MasterProfileResponse(Long id, String name, String city, String phone, String facebookPageId,
                                 String description, String address, List<String> photos, String createdAt) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.phone = phone;
        this.facebookPageId = facebookPageId;
        this.description = description;
        this.address = address;
        this.photos = photos;
        this.createdAt = createdAt;
    }

    public static MasterProfileResponse fromDomain(MasterProfile profile) {
        List<String> photoList = Collections.emptyList();
        if (profile.getPhotos() != null && !profile.getPhotos().isBlank()) {
            photoList = Arrays.stream(profile.getPhotos().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }

        return new MasterProfileResponse(
                profile.getId(),
                profile.getName(),
                profile.getCity(),
                profile.getPhone(),
                profile.getFacebookPageId(),
                profile.getDescription(),
                profile.getAddress(),
                photoList,
                profile.getCreatedAt() != null ? profile.getCreatedAt().toString() : null
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFacebookPageId() {
        return facebookPageId;
    }

    public void setFacebookPageId(String facebookPageId) {
        this.facebookPageId = facebookPageId;
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

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
