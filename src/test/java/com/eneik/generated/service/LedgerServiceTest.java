package com.eneik.generated.service;

import com.eneik.generated.domain.LedgerEntry;
import com.eneik.generated.domain.PaymentMethod;
import com.eneik.generated.domain.PaymentStatus;
import com.eneik.generated.dto.CreateLedgerEntryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LedgerServiceTest {

    private LedgerService ledgerService;
    private final Instant fixedInstant = Instant.parse("2026-10-06T12:00:00Z");
    private final Clock fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        ledgerService = new LedgerService(fixedClock);
    }

    @Test
    void testCreateAndGetLedgerEntry() {
        CreateLedgerEntryRequest request = new CreateLedgerEntryRequest();
        request.setProfessionalId("prof-100");
        request.setAmount(new BigDecimal("75.00"));
        request.setCurrency("GEL");
        request.setPaymentMethod(PaymentMethod.TBC_TRANSFER);
        request.setNotes("Payment for haircut");

        LedgerEntry entry = ledgerService.createLedgerEntry("book-100", request);

        assertNotNull(entry);
        assertEquals("book-100", entry.getBookingId());
        assertEquals("prof-100", entry.getProfessionalId());
        assertEquals(new BigDecimal("75.00"), entry.getAmount());
        assertEquals("GEL", entry.getCurrency());
        assertEquals(PaymentMethod.TBC_TRANSFER, entry.getPaymentMethod());
        assertEquals(PaymentStatus.PENDING, entry.getStatus());
        assertEquals(fixedInstant, entry.getCreatedAt());
        assertEquals(fixedInstant, entry.getUpdatedAt());

        Optional<LedgerEntry> retrieved = ledgerService.getLedgerEntry("book-100");
        assertTrue(retrieved.isPresent());
        assertEquals(PaymentStatus.PENDING, retrieved.get().getStatus());
    }

    @Test
    void testDuplicateBookingCreationFails() {
        CreateLedgerEntryRequest request = new CreateLedgerEntryRequest();
        request.setProfessionalId("prof-100");
        request.setAmount(new BigDecimal("50.00"));
        request.setPaymentMethod(PaymentMethod.CASH);

        ledgerService.createLedgerEntry("book-dup", request);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                ledgerService.createLedgerEntry("book-dup", request));
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    void testUpdatePaymentStatusSuccess() {
        CreateLedgerEntryRequest request = new CreateLedgerEntryRequest();
        request.setProfessionalId("prof-100");
        request.setAmount(new BigDecimal("100.00"));
        request.setPaymentMethod(PaymentMethod.BOG_TRANSFER);

        ledgerService.createLedgerEntry("book-200", request);

        LedgerEntry updated = ledgerService.updatePaymentStatus(
                "book-200",
                PaymentStatus.PENDING,
                PaymentStatus.SETTLED,
                "Transfer verified in BOG app"
        );

        assertEquals(PaymentStatus.SETTLED, updated.getStatus());
        assertEquals("Transfer verified in BOG app", updated.getNotes());
    }

    @Test
    void testUpdatePaymentStatusExpectedMismatchFails() {
        CreateLedgerEntryRequest request = new CreateLedgerEntryRequest();
        request.setProfessionalId("prof-100");
        request.setAmount(new BigDecimal("100.00"));
        request.setPaymentMethod(PaymentMethod.BOG_TRANSFER);

        ledgerService.createLedgerEntry("book-300", request);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                ledgerService.updatePaymentStatus(
                        "book-300",
                        PaymentStatus.MANUALLY_VERIFIED, // mismatch vs actual PENDING
                        PaymentStatus.SETTLED,
                        "Notes"
                ));
        assertTrue(ex.getMessage().contains("Status mismatch"));
    }

    @Test
    void testUpdateTerminalStatusFails() {
        CreateLedgerEntryRequest request = new CreateLedgerEntryRequest();
        request.setProfessionalId("prof-100");
        request.setAmount(new BigDecimal("100.00"));
        request.setPaymentMethod(PaymentMethod.CASH);

        ledgerService.createLedgerEntry("book-term", request);
        ledgerService.updatePaymentStatus("book-term", null, PaymentStatus.SETTLED, "Settled");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                ledgerService.updatePaymentStatus("book-term", null, PaymentStatus.PENDING, "Reopen"));
        assertTrue(ex.getMessage().contains("terminal state"));
    }

    @Test
    void testUpdateNotFoundFails() {
        assertThrows(NoSuchElementException.class, () ->
                ledgerService.updatePaymentStatus("non-existent", null, PaymentStatus.SETTLED, null));
    }

    @Test
    void testListLedgerEntries() {
        CreateLedgerEntryRequest req1 = new CreateLedgerEntryRequest();
        req1.setProfessionalId("prof-A");
        req1.setAmount(new BigDecimal("10.00"));
        req1.setPaymentMethod(PaymentMethod.CASH);
        ledgerService.createLedgerEntry("b-1", req1);

        CreateLedgerEntryRequest req2 = new CreateLedgerEntryRequest();
        req2.setProfessionalId("prof-B");
        req2.setAmount(new BigDecimal("20.00"));
        req2.setPaymentMethod(PaymentMethod.TBC_TRANSFER);
        ledgerService.createLedgerEntry("b-2", req2);

        List<LedgerEntry> all = ledgerService.listLedgerEntries(null);
        assertEquals(2, all.size());

        List<LedgerEntry> profAEntries = ledgerService.listLedgerEntries("prof-A");
        assertEquals(1, profAEntries.size());
        assertEquals("b-1", profAEntries.get(0).getBookingId());
    }
}
