package com.eneik.generated.controller;

import com.eneik.generated.service.VoiceBookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@io.zonky.test.db.AutoConfigureEmbeddedDatabase(type = io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
@AutoConfigureMockMvc
public class VoiceBridgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VoiceBookingService voiceBookingService;

    @BeforeEach
    public void setUp() {
        voiceBookingService.setIdGenerator(() -> "test-booking-id-1001");
    }

    @Test
    public void testProposeSlotReturnsSynthesizedGeorgianAudioResponse() throws Exception {
        String requestJson = """
            {
                "masterId": "master-georgia-01",
                "serviceName": "Haircut & Styling",
                "slotTime": "2026-10-15 11:00"
            }
            """;

        mockMvc.perform(post("/api/voice/bridge/propose-slot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bookingId").value("test-booking-id-1001"))
            .andExpect(jsonPath("$.masterId").value("master-georgia-01"))
            .andExpect(jsonPath("$.serviceName").value("Haircut & Styling"))
            .andExpect(jsonPath("$.slotTime").value("2026-10-15 11:00"))
            .andExpect(jsonPath("$.status").value("PROPOSED"))
            .andExpect(jsonPath("$.textResponse", containsString("შემოთავაზებული დრო")))
            .andExpect(jsonPath("$.audioBase64", notNullValue()))
            .andExpect(jsonPath("$.audioFormat").value("audio/wav"));
    }

    @Test
    public void testConfirmBookingWithVoiceMarksBookingComplete() throws Exception {
        // Propose slot first
        String proposeJson = """
            {
                "masterId": "master-georgia-01",
                "serviceName": "Haircut & Styling",
                "slotTime": "2026-10-15 11:00"
            }
            """;

        mockMvc.perform(post("/api/voice/bridge/propose-slot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(proposeJson))
            .andExpect(status().isOk());

        // Process voice confirmation
        String confirmJson = """
            {
                "bookingId": "test-booking-id-1001",
                "voiceCommand": "დიახ"
            }
            """;

        mockMvc.perform(post("/api/voice/bridge/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(confirmJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bookingId").value("test-booking-id-1001"))
            .andExpect(jsonPath("$.status").value("COMPLETED"))
            .andExpect(jsonPath("$.message").value("Booking confirmed and marked complete"));
    }

    @Test
    public void testConfirmNonExistentBookingReturnsNotFound() throws Exception {
        String confirmJson = """
            {
                "bookingId": "non-existent-id",
                "voiceCommand": "confirm"
            }
            """;

        mockMvc.perform(post("/api/voice/bridge/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(confirmJson))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Booking not found"));
    }
}
