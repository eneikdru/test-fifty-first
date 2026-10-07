package com.eneik.generated.controller;

import com.eneik.generated.model.AvailabilitySlot;
import com.eneik.generated.model.BookingEntity;
import com.eneik.generated.model.MasterProfile;
import com.eneik.generated.service.BookingEngineService;
import com.eneik.generated.service.BookingEngineService.SlotUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class BookingEngineController {

    private final BookingEngineService bookingEngineService;
    private final Clock clock;

    @org.springframework.beans.factory.annotation.Autowired
    public BookingEngineController(BookingEngineService bookingEngineService) {
        this(bookingEngineService, Clock.systemUTC());
    }

    public BookingEngineController(BookingEngineService bookingEngineService, Clock clock) {
        this.bookingEngineService = bookingEngineService;
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    @GetMapping("/masters")
    public ResponseEntity<List<MasterProfile>> searchMasters(@RequestParam(value = "city", required = false) String city) {
        List<MasterProfile> masters = bookingEngineService.searchMasters(city);
        return ResponseEntity.ok(masters);
    }

    @GetMapping("/masters/{masterId}/slots")
    public ResponseEntity<List<AvailabilitySlot>> getMasterSlots(@PathVariable("masterId") Long masterId,
                                                                 @RequestParam(value = "status", required = false) String status) {
        List<AvailabilitySlot> slots = bookingEngineService.getMasterSlots(masterId, status);
        return ResponseEntity.ok(slots);
    }

    @PostMapping("/bookings/hold")
    public ResponseEntity<?> createHold(@RequestBody HoldRequestDto request) {
        if (request == null || request.getSlotId() == null || request.getMasterId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", "BAD_REQUEST", "message", "slotId and masterId are required", "timestamp", OffsetDateTime.now(clock).toString()));
        }

        try {
            BookingEntity booking = bookingEngineService.createHold(
                    request.getSlotId(),
                    request.getMasterId(),
                    request.getCustomerPhone() != null ? request.getCustomerPhone() : "",
                    request.getServiceName() != null ? request.getServiceName() : "General Service",
                    request.getPriceGel() != null ? request.getPriceGel() : 0.0
            );
            return ResponseEntity.ok(booking);
        } catch (SlotUnavailableException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("code", "SLOT_UNAVAILABLE", "message", e.getMessage(), "timestamp", OffsetDateTime.now(clock).toString()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", "INVALID_ARGUMENT", "message", e.getMessage(), "timestamp", OffsetDateTime.now(clock).toString()));
        }
    }

    @PostMapping("/bookings/confirm")
    public ResponseEntity<?> confirmBooking(@RequestBody ConfirmBookingRequestDto request) {
        if (request == null || request.getBookingId() == null || request.getBookingId().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", "BAD_REQUEST", "message", "bookingId is required", "timestamp", OffsetDateTime.now(clock).toString()));
        }

        try {
            BookingEntity booking = bookingEngineService.confirmBooking(request.getBookingId());
            return ResponseEntity.ok(booking);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("code", "CONFIRMATION_CONFLICT", "message", e.getMessage(), "timestamp", OffsetDateTime.now(clock).toString()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("code", "NOT_FOUND", "message", e.getMessage(), "timestamp", OffsetDateTime.now(clock).toString()));
        }
    }

    public static class HoldRequestDto {
        private Long slotId;
        private Long masterId;
        private String customerPhone;
        private String serviceName;
        private Double priceGel;

        public HoldRequestDto() {}

        public HoldRequestDto(Long slotId, Long masterId, String customerPhone, String serviceName, Double priceGel) {
            this.slotId = slotId;
            this.masterId = masterId;
            this.customerPhone = customerPhone;
            this.serviceName = serviceName;
            this.priceGel = priceGel;
        }

        public Long getSlotId() { return slotId; }
        public void setSlotId(Long slotId) { this.slotId = slotId; }

        public Long getMasterId() { return masterId; }
        public void setMasterId(Long masterId) { this.masterId = masterId; }

        public String getCustomerPhone() { return customerPhone; }
        public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }

        public Double getPriceGel() { return priceGel; }
        public void setPriceGel(Double priceGel) { this.priceGel = priceGel; }
    }

    public static class ConfirmBookingRequestDto {
        private String bookingId;

        public ConfirmBookingRequestDto() {}

        public ConfirmBookingRequestDto(String bookingId) {
            this.bookingId = bookingId;
        }

        public String getBookingId() { return bookingId; }
        public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    }
}
