package com.eneik.generated.dto;

public class MasterServiceResponse {
    private Long id;
    private Long masterId;
    private String name;
    private String description;
    private Double priceGel;
    private Integer durationMinutes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPriceGel() {
        return priceGel;
    }

    public void setPriceGel(Double priceGel) {
        this.priceGel = priceGel;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
