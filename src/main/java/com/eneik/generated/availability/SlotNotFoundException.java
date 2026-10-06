package com.eneik.generated.availability;

public class SlotNotFoundException extends RuntimeException {
    public SlotNotFoundException(Long id) {
        super("Availability slot not found with id: " + id);
    }
}
