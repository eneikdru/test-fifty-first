package com.eneik.generated.booking;

public enum SlotStatus {
    FREE,
    HELD,
    BOOKED,
    NO_SHOW,
    CANCELLED;

    public boolean canTransitionTo(SlotStatus target) {
        if (target == null) {
            return false;
        }
        if (this == target) {
            return true;
        }
        return switch (this) {
            case FREE -> target == HELD || target == CANCELLED;
            case HELD -> target == BOOKED || target == FREE || target == CANCELLED;
            case BOOKED -> target == NO_SHOW || target == CANCELLED;
            case NO_SHOW, CANCELLED -> false;
        };
    }
}
