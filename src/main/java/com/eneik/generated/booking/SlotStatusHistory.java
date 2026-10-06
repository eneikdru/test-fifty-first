package com.eneik.generated.booking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "slot_status_history")
public class SlotStatusHistory {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "slot_id", length = 36, nullable = false)
    private String slotId;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 20)
    private SlotStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", length = 20, nullable = false)
    private SlotStatus newStatus;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    public SlotStatusHistory() {
    }

    public SlotStatusHistory(String id, String slotId, SlotStatus previousStatus, SlotStatus newStatus, String reason, Instant changedAt) {
        this.id = id;
        this.slotId = slotId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.reason = reason;
        this.changedAt = changedAt;
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

    public SlotStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(SlotStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public SlotStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(SlotStatus newStatus) {
        this.newStatus = newStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlotStatusHistory history = (SlotStatusHistory) o;
        return Objects.equals(id, history.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
