package com.eneik.generated.dto;

public class FacebookImportRequest {
    private String facebookToken;
    private String facebookPageId;
    private Long masterId;
    private String phone;

    public FacebookImportRequest() {
    }

    public FacebookImportRequest(String facebookToken, String facebookPageId, Long masterId, String phone) {
        this.facebookToken = facebookToken;
        this.facebookPageId = facebookPageId;
        this.masterId = masterId;
        this.phone = phone;
    }

    public String getFacebookToken() {
        return facebookToken;
    }

    public void setFacebookToken(String facebookToken) {
        this.facebookToken = facebookToken;
    }

    public String getFacebookPageId() {
        return facebookPageId;
    }

    public void setFacebookPageId(String facebookPageId) {
        this.facebookPageId = facebookPageId;
    }

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
