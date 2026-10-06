package com.eneik.generated.service;

import com.eneik.generated.domain.LedgerEntry;
import com.eneik.generated.domain.PaymentStatus;
import com.eneik.generated.dto.CreateLedgerEntryRequest;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class LedgerService {

    private final Map<String, LedgerEntry> storage = new ConcurrentHashMap<>();
    private final Clock clock;

    public LedgerService(Clock clock) {
        this.clock = clock;
    }

    public Optional<LedgerEntry> getLedgerEntry(String bookingId) {
        if (bookingId == null || bookingId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(bookingId)).map(LedgerEntry::copy);
    }

    public LedgerEntry createLedgerEntry(String bookingId, CreateLedgerEntryRequest request) {
        if (bookingId == null || bookingId.isBlank()) {
            throw new IllegalArgumentException("Booking ID must not be blank");
        }
        if (request == null || request.getProfessionalId() == null || request.getProfessionalId().isBlank()) {
            throw new IllegalArgumentException("Professional ID must not be blank");
        }
        if (request.getAmount() == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
        if (request.getPaymentMethod() == null) {
            throw new IllegalArgumentException("Payment method must not be null");
        }

        Instant now = Instant.now(clock);
        String currency = (request.getCurrency() != null && !request.getCurrency().isBlank())
                ? request.getCurrency()
                : "GEL";

        LedgerEntry newEntry = new LedgerEntry(
                bookingId,
                request.getProfessionalId(),
                request.getAmount(),
                currency,
                request.getPaymentMethod(),
                PaymentStatus.PENDING,
                request.getNotes(),
                now,
                now
        );

        LedgerEntry existing = storage.putIfAbsent(bookingId, newEntry);
        if (existing != null) {
            throw new IllegalStateException("Ledger entry already exists for booking: " + bookingId);
        }

        return newEntry.copy();
    }

    public LedgerEntry updatePaymentStatus(String bookingId, PaymentStatus expectedStatus, PaymentStatus newStatus, String notes) {
        if (bookingId == null || bookingId.isBlank()) {
            throw new IllegalArgumentException("Booking ID must not be blank");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("New status must not be null");
        }

        Instant now = Instant.now(clock);
        AtomicReference<LedgerEntry> resultRef = new AtomicReference<>();

        storage.compute(bookingId, (id, current) -> {
            if (current == null) {
                throw new NoSuchElementException("Ledger entry not found for booking: " + bookingId);
            }

            if (current.getStatus().isTerminal()) {
                throw new IllegalStateException("Cannot update ledger entry in terminal state: " + current.getStatus());
            }

            if (expectedStatus != null && current.getStatus() != expectedStatus) {
                throw new IllegalStateException("Status mismatch: expected " + expectedStatus + " but found " + current.getStatus());
            }

            LedgerEntry updated = current.copy();
            updated.setStatus(newStatus);
            if (notes != null) {
                updated.setNotes(notes);
            }
            updated.setUpdatedAt(now);

            resultRef.set(updated);
            return updated;
        });

        return resultRef.get();
    }

    public List<LedgerEntry> listLedgerEntries(String professionalId) {
        return storage.values().stream()
                .filter(entry -> professionalId == null || professionalId.isBlank() || professionalId.equals(entry.getProfessionalId()))
                .map(LedgerEntry::copy)
                .collect(Collectors.toList());
    }

    public void clear() {
        storage.clear();
    }
}
