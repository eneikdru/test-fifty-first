package com.eneik.generated.availability;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "slot_status_history")
public class SlotStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slot_id", nullable = false)
    private Long slotId;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private SlotStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private SlotStatus newStatus;

    @Column(name = "changed_by")
    private String changedBy;

    @Column(name = "reason")
    private String reason;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt = Instant.now();

    public SlotStatusHistory() {
    }

    public SlotStatusHistory(Long slotId, SlotStatus previousStatus, SlotStatus newStatus, String changedBy, String reason, Instant changedAt) {
        this.slotId = slotId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.reason = reason;
        this.changedAt = changedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
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

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
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
}
