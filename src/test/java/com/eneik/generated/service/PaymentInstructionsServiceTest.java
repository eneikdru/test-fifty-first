package com.eneik.generated.service;

import com.eneik.generated.domain.PaymentInstructions;
import com.eneik.generated.dto.PaymentInstructionsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PaymentInstructionsServiceTest {

    private PaymentInstructionsService service;
    private final Instant fixedInstant = Instant.parse("2026-10-06T12:00:00Z");
    private final Clock fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        service = new PaymentInstructionsService(fixedClock);
    }

    @Test
    void testSaveAndGetPaymentInstructions() {
        PaymentInstructionsRequest request = new PaymentInstructionsRequest();
        request.setAccountHolderName("Nino Giorgadze");
        request.setTbcIban("GE29TB7012345678901234");
        request.setBogIban("GE29BG0000000123456789");
        request.setAcceptsCash(true);
        request.setInstructionsNote("TBC transfer preferred");

        PaymentInstructions saved = service.saveOrUpdatePaymentInstructions("prof-1", request);

        assertNotNull(saved);
        assertEquals("prof-1", saved.getProfessionalId());
        assertEquals("Nino Giorgadze", saved.getAccountHolderName());
        assertEquals("GE29TB7012345678901234", saved.getTbcIban());
        assertEquals("GE29BG0000000123456789", saved.getBogIban());
        assertTrue(saved.isAcceptsCash());
        assertEquals(fixedInstant, saved.getUpdatedAt());

        Optional<PaymentInstructions> retrieved = service.getPaymentInstructions("prof-1");
        assertTrue(retrieved.isPresent());
        assertEquals("Nino Giorgadze", retrieved.get().getAccountHolderName());
    }

    @Test
    void testGetNotFound() {
        Optional<PaymentInstructions> result = service.getPaymentInstructions("unknown-prof");
        assertTrue(result.isEmpty());
    }

    @Test
    void testSaveInvalidParameters() {
        assertThrows(IllegalArgumentException.class, () ->
                service.saveOrUpdatePaymentInstructions("", new PaymentInstructionsRequest()));

        PaymentInstructionsRequest emptyNameReq = new PaymentInstructionsRequest();
        emptyNameReq.setAccountHolderName("  ");
        assertThrows(IllegalArgumentException.class, () ->
                service.saveOrUpdatePaymentInstructions("prof-1", emptyNameReq));
    }
}
