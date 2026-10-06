package com.eneik.generated.controller;

import com.eneik.generated.domain.Booking;
import com.eneik.generated.dto.BookingRequest;
import com.eneik.generated.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequest request) {
        Booking booking = bookingService.processBooking(
                request.masterId(),
                request.slotId(),
                request.customerName(),
                request.customerPhone()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }
}
