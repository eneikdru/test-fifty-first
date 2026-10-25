package com.eneik.generated.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "master_services")
public class MasterService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "master_id", nullable = false)
    private Long masterId;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "name_ka")
    private String nameKa;

    @Column(name = "name_ru")
    private String nameRu;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    @Column(name = "description_ka", columnDefinition = "TEXT")
    private String descriptionKa;

    @Column(name = "description_ru", columnDefinition = "TEXT")
    private String descriptionRu;

    @Column(name = "price_gel", nullable = false)
    private Double priceGel;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public MasterService() {
    }

    public MasterService(Long masterId, String nameEn, String nameKa, String nameRu, String descriptionEn, String descriptionKa, String descriptionRu, Double priceGel, Integer durationMinutes, OffsetDateTime createdAt) {
        this.masterId = masterId;
        this.nameEn = nameEn;
        this.nameKa = nameKa;
        this.nameRu = nameRu;
        this.descriptionEn = descriptionEn;
        this.descriptionKa = descriptionKa;
        this.descriptionRu = descriptionRu;
        this.priceGel = priceGel;
        this.durationMinutes = durationMinutes;
        this.createdAt = createdAt;
    }

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

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameKa() {
        return nameKa;
    }

    public void setNameKa(String nameKa) {
        this.nameKa = nameKa;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionKa() {
        return descriptionKa;
    }

    public void setDescriptionKa(String descriptionKa) {
        this.descriptionKa = descriptionKa;
    }

    public String getDescriptionRu() {
        return descriptionRu;
    }

    public void setDescriptionRu(String descriptionRu) {
        this.descriptionRu = descriptionRu;
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
        MasterService that = (MasterService) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
