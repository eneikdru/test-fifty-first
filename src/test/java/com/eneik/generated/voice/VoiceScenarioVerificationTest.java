package com.eneik.generated.voice;

import com.eneik.generated.service.VoiceBookingService;
import com.eneik.generated.service.VoiceBookingService.VoiceBooking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class VoiceScenarioVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VoiceBookingService voiceBookingService;

    @BeforeEach
    void setUp() {
        voiceBookingService.setIdGenerator(() -> "voice-scenario-booking-999");
    }

    @Test
    @DisplayName("Given pre-recorded Georgian booking requests, when fed to the pipeline, then bookings are created and confirmed")
    void testPreRecordedGeorgianBookingRequestCreatesAndConfirmsBooking() throws Exception {
        // Propose slot request representing pre-recorded Georgian booking request audio / parameters
        String proposeRequestJson = """
            {
                "masterId": "master-tbilisi-88",
                "serviceName": "Haircut",
                "slotTime": "2026-10-20 14:00"
            }
            """;

        mockMvc.perform(post("/api/voice/bridge/propose-slot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(proposeRequestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bookingId").value("voice-scenario-booking-999"))
            .andExpect(jsonPath("$.masterId").value("master-tbilisi-88"))
            .andExpect(jsonPath("$.serviceName").value("Haircut"))
            .andExpect(jsonPath("$.slotTime").value("2026-10-20 14:00"))
            .andExpect(jsonPath("$.status").value("PROPOSED"))
            .andExpect(jsonPath("$.textResponse", containsString("შემოთავაზებული დრო")))
            .andExpect(jsonPath("$.audioBase64", notNullValue()))
            .andExpect(jsonPath("$.audioFormat").value("audio/wav"));

        // Confirm booking with pre-recorded Georgian affirmative voice command ("დიახ")
        String confirmRequestJson = """
            {
                "bookingId": "voice-scenario-booking-999",
                "voiceCommand": "დიახ"
            }
            """;

        mockMvc.perform(post("/api/voice/bridge/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(confirmRequestJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bookingId").value("voice-scenario-booking-999"))
            .andExpect(jsonPath("$.status").value("COMPLETED"))
            .andExpect(jsonPath("$.message").value("Booking confirmed and marked complete"));

        // Verify service state
        VoiceBooking booking = voiceBookingService.getBooking("voice-scenario-booking-999").orElse(null);
        assertNotNull(booking);
        assertEquals(VoiceBookingService.BookingStatus.COMPLETED, booking.getStatus());
    }

    @Test
    @DisplayName("Given noisy audio or unknown booking ID, when tested, then fallback behavior returns HTTP 404 error response")
    void testNoisyAudioOrUnknownBookingFallback() throws Exception {
        // Non-existent or corrupted voice confirmation payload simulation
        String noisyOrInvalidConfirmJson = """
            {
                "bookingId": "unknown-noisy-audio-id",
                "voiceCommand": "??? [noisy audio signal] ???"
            }
            """;

        mockMvc.perform(post("/api/voice/bridge/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(noisyOrInvalidConfirmJson))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Booking not found"))
            .andExpect(jsonPath("$.bookingId").value("unknown-noisy-audio-id"));
    }
}
