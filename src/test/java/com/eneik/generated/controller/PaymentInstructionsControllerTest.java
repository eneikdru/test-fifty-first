package com.eneik.generated.controller;

import com.eneik.generated.domain.PaymentInstructions;
import com.eneik.generated.dto.PaymentInstructionsRequest;
import com.eneik.generated.service.PaymentInstructionsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PaymentInstructionsControllerTest {

    private MockMvc mockMvc;
    private PaymentInstructionsService service;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        service = mock(PaymentInstructionsService.class);
        PaymentInstructionsController controller = new PaymentInstructionsController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetPaymentInstructionsSuccess() throws Exception {
        PaymentInstructions instructions = new PaymentInstructions(
                "prof-1",
                "Giorgi Beridze",
                "GE29TB7012345678901234",
                "GE29BG0000000123456789",
                true,
                "Transfer note",
                Instant.parse("2026-10-06T12:00:00Z")
        );

        when(service.getPaymentInstructions("prof-1")).thenReturn(Optional.of(instructions));

        mockMvc.perform(get("/api/v1/professionals/prof-1/payment-instructions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.professionalId").value("prof-1"))
                .andExpect(jsonPath("$.accountHolderName").value("Giorgi Beridze"))
                .andExpect(jsonPath("$.tbcIban").value("GE29TB7012345678901234"))
                .andExpect(jsonPath("$.bogIban").value("GE29BG0000000123456789"))
                .andExpect(jsonPath("$.acceptsCash").value(true));
    }

    @Test
    void testGetPaymentInstructionsNotFound() throws Exception {
        when(service.getPaymentInstructions("unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/professionals/unknown/payment-instructions"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdatePaymentInstructionsSuccess() throws Exception {
        PaymentInstructionsRequest request = new PaymentInstructionsRequest();
        request.setAccountHolderName("Giorgi Beridze");
        request.setTbcIban("GE29TB7012345678901234");
        request.setBogIban("GE29BG0000000123456789");
        request.setAcceptsCash(true);

        PaymentInstructions updated = new PaymentInstructions(
                "prof-1",
                "Giorgi Beridze",
                "GE29TB7012345678901234",
                "GE29BG0000000123456789",
                true,
                null,
                Instant.parse("2026-10-06T12:00:00Z")
        );

        when(service.saveOrUpdatePaymentInstructions(eq("prof-1"), any())).thenReturn(updated);

        mockMvc.perform(put("/api/v1/professionals/prof-1/payment-instructions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.professionalId").value("prof-1"))
                .andExpect(jsonPath("$.accountHolderName").value("Giorgi Beridze"));
    }
}
