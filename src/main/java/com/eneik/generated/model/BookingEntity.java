package com.eneik.generated.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "bookings")
public class BookingEntity {

    @Id
    @Column(name = "id", nullable = false, length = 64)
    private String id;

    @Column(name = "slot_id", nullable = false)
    private Long slotId;

    @Column(name = "master_id", nullable = false)
    private Long masterId;

    @Column(name = "customer_phone", nullable = false, length = 32)
    private String customerPhone;

    @Column(name = "service_name", nullable = false, length = 128)
    private String serviceName;

    @Column(name = "price_gel", nullable = false, columnDefinition = "NUMERIC(10, 2)")
    private Double priceGel;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    @Column(name = "hold_expires_at")
    private OffsetDateTime holdExpiresAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public BookingEntity() {
    }

    public BookingEntity(String id, Long slotId, Long masterId, String customerPhone, String serviceName, Double priceGel, String status, OffsetDateTime holdExpiresAt, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.slotId = slotId;
        this.masterId = masterId;
        this.customerPhone = customerPhone;
        this.serviceName = serviceName;
        this.priceGel = priceGel;
        this.status = status;
        this.holdExpiresAt = holdExpiresAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Double getPriceGel() {
        return priceGel;
    }

    public void setPriceGel(Double priceGel) {
        this.priceGel = priceGel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getHoldExpiresAt() {
        return holdExpiresAt;
    }

    public void setHoldExpiresAt(OffsetDateTime holdExpiresAt) {
        this.holdExpiresAt = holdExpiresAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingEntity that = (BookingEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
