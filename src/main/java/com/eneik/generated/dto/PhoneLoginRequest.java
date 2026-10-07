package com.eneik.generated.dto;

public class PhoneLoginRequest {
    private String phone;
    private String name;
    private String city;

    public PhoneLoginRequest() {
    }

    public PhoneLoginRequest(String phone, String name, String city) {
        this.phone = phone;
        this.name = name;
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
}
