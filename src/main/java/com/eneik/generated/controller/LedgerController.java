package com.eneik.generated.controller;

import com.eneik.generated.domain.LedgerEntry;
import com.eneik.generated.dto.CreateLedgerEntryRequest;
import com.eneik.generated.dto.UpdatePaymentStatusRequest;
import com.eneik.generated.service.LedgerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class LedgerController {

    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @GetMapping("/api/v1/bookings/{bookingId}/payment-state")
    public ResponseEntity<LedgerEntry> getBookingPaymentState(@PathVariable String bookingId) {
        return ledgerService.getLedgerEntry(bookingId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/v1/bookings/{bookingId}/payment-state")
    public ResponseEntity<?> createBookingPaymentState(@PathVariable String bookingId,
                                                        @RequestBody CreateLedgerEntryRequest request) {
        try {
            LedgerEntry entry = ledgerService.createLedgerEntry(bookingId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(entry);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PatchMapping("/api/v1/bookings/{bookingId}/payment-state")
    public ResponseEntity<?> updateBookingPaymentStatus(@PathVariable String bookingId,
                                                         @RequestBody UpdatePaymentStatusRequest request) {
        try {
            LedgerEntry updated = ledgerService.updatePaymentStatus(
                    bookingId,
                    request.getExpectedStatus(),
                    request.getNewStatus(),
                    request.getNotes()
            );
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/api/v1/ledger/entries")
    public ResponseEntity<List<LedgerEntry>> listLedgerEntries(
            @RequestParam(required = false) String professionalId) {
        List<LedgerEntry> entries = ledgerService.listLedgerEntries(professionalId);
        return ResponseEntity.ok(entries);
    }
}
