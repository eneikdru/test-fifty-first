package com.eneik.generated.messaging.controller;

import com.eneik.generated.messaging.domain.Booking;
import com.eneik.generated.messaging.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public record CreateBookingRequest(
            String customerId,
            String customerPhone,
            String customerEmail,
            String masterId,
            String serviceName,
            Instant appointmentTime
    ) {}

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody CreateBookingRequest request) {
        Booking booking = bookingService.createBooking(
                request.customerId(),
                request.customerPhone(),
                request.customerEmail(),
                request.masterId(),
                request.serviceName(),
                request.appointmentTime()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBooking(@PathVariable String id) {
        return bookingService.getBookingRepository().findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getBookingRepository().findAll());
    }
}
