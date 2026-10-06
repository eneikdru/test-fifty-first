package com.eneik.generated.booking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "slot_holds")
public class SlotHold {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "slot_id", length = 36, nullable = false)
    private String slotId;

    @Column(name = "held_by", length = 64, nullable = false)
    private String heldBy;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public SlotHold() {
    }

    public SlotHold(String id, String slotId, String heldBy, Instant expiresAt, Instant createdAt) {
        this.id = id;
        this.slotId = slotId;
        this.heldBy = heldBy;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getHeldBy() {
        return heldBy;
    }

    public void setHeldBy(String heldBy) {
        this.heldBy = heldBy;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlotHold slotHold = (SlotHold) o;
        return Objects.equals(id, slotHold.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
