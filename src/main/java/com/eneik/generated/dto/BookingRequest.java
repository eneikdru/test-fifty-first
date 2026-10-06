package com.eneik.generated.dto;

public record BookingRequest(
        String masterId,
        Long slotId,
        String customerName,
        String customerPhone
) {}
