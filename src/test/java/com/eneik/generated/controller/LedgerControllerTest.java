package com.eneik.generated.controller;

import com.eneik.generated.domain.LedgerEntry;
import com.eneik.generated.domain.PaymentMethod;
import com.eneik.generated.domain.PaymentStatus;
import com.eneik.generated.dto.CreateLedgerEntryRequest;
import com.eneik.generated.dto.UpdatePaymentStatusRequest;
import com.eneik.generated.service.LedgerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LedgerControllerTest {

    private MockMvc mockMvc;
    private LedgerService ledgerService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ledgerService = mock(LedgerService.class);
        LedgerController controller = new LedgerController(ledgerService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetBookingPaymentStateSuccess() throws Exception {
        LedgerEntry entry = new LedgerEntry(
                "book-1",
                "prof-1",
                new BigDecimal("50.00"),
                "GEL",
                PaymentMethod.TBC_TRANSFER,
                PaymentStatus.PENDING,
                "Note",
                Instant.parse("2026-10-06T12:00:00Z"),
                Instant.parse("2026-10-06T12:00:00Z")
        );

        when(ledgerService.getLedgerEntry("book-1")).thenReturn(Optional.of(entry));

        mockMvc.perform(get("/api/v1/bookings/book-1/payment-state"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value("book-1"))
                .andExpect(jsonPath("$.professionalId").value("prof-1"))
                .andExpect(jsonPath("$.paymentMethod").value("TBC_TRANSFER"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testGetBookingPaymentStateNotFound() throws Exception {
        when(ledgerService.getLedgerEntry("unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/bookings/unknown/payment-state"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateBookingPaymentStateSuccess() throws Exception {
        CreateLedgerEntryRequest request = new CreateLedgerEntryRequest();
        request.setProfessionalId("prof-1");
        request.setAmount(new BigDecimal("50.00"));
        request.setCurrency("GEL");
        request.setPaymentMethod(PaymentMethod.TBC_TRANSFER);

        LedgerEntry entry = new LedgerEntry(
                "book-1",
                "prof-1",
                new BigDecimal("50.00"),
                "GEL",
                PaymentMethod.TBC_TRANSFER,
                PaymentStatus.PENDING,
                null,
                Instant.parse("2026-10-06T12:00:00Z"),
                Instant.parse("2026-10-06T12:00:00Z")
        );

        when(ledgerService.createLedgerEntry(eq("book-1"), any())).thenReturn(entry);

        mockMvc.perform(post("/api/v1/bookings/book-1/payment-state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value("book-1"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testCreateBookingPaymentStateConflict() throws Exception {
        CreateLedgerEntryRequest request = new CreateLedgerEntryRequest();
        request.setProfessionalId("prof-1");
        request.setAmount(new BigDecimal("50.00"));
        request.setPaymentMethod(PaymentMethod.CASH);

        when(ledgerService.createLedgerEntry(eq("book-dup"), any()))
                .thenThrow(new IllegalStateException("Ledger entry already exists for booking: book-dup"));

        mockMvc.perform(post("/api/v1/bookings/book-dup/payment-state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void testUpdateBookingPaymentStatusSuccess() throws Exception {
        UpdatePaymentStatusRequest request = new UpdatePaymentStatusRequest();
        request.setExpectedStatus(PaymentStatus.PENDING);
        request.setNewStatus(PaymentStatus.SETTLED);
        request.setNotes("Settled via TBC");

        LedgerEntry updated = new LedgerEntry(
                "book-1",
                "prof-1",
                new BigDecimal("50.00"),
                "GEL",
                PaymentMethod.TBC_TRANSFER,
                PaymentStatus.SETTLED,
                "Settled via TBC",
                Instant.parse("2026-10-06T12:00:00Z"),
                Instant.parse("2026-10-06T12:01:00Z")
        );

        when(ledgerService.updatePaymentStatus(eq("book-1"), eq(PaymentStatus.PENDING), eq(PaymentStatus.SETTLED), eq("Settled via TBC")))
                .thenReturn(updated);

        mockMvc.perform(patch("/api/v1/bookings/book-1/payment-state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SETTLED"))
                .andExpect(jsonPath("$.notes").value("Settled via TBC"));
    }

    @Test
    void testUpdateBookingPaymentStatusNotFound() throws Exception {
        UpdatePaymentStatusRequest request = new UpdatePaymentStatusRequest();
        request.setNewStatus(PaymentStatus.SETTLED);

        when(ledgerService.updatePaymentStatus(eq("unknown"), any(), eq(PaymentStatus.SETTLED), any()))
                .thenThrow(new NoSuchElementException("Not found"));

        mockMvc.perform(patch("/api/v1/bookings/unknown/payment-state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testListLedgerEntries() throws Exception {
        LedgerEntry entry = new LedgerEntry(
                "book-1",
                "prof-1",
                new BigDecimal("50.00"),
                "GEL",
                PaymentMethod.TBC_TRANSFER,
                PaymentStatus.PENDING,
                null,
                Instant.parse("2026-10-06T12:00:00Z"),
                Instant.parse("2026-10-06T12:00:00Z")
        );

        when(ledgerService.listLedgerEntries("prof-1")).thenReturn(List.of(entry));

        mockMvc.perform(get("/api/v1/ledger/entries?professionalId=prof-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].bookingId").value("book-1"));
    }
}
