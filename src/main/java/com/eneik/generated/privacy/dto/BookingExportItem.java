package com.eneik.generated.privacy.dto;

import java.time.Instant;

public record BookingExportItem(
        String bookingId,
        String masterId,
        String serviceName,
        Double priceGel,
        Instant startTime,
        Instant endTime,
        String status,
        String paymentMethod
) {}
