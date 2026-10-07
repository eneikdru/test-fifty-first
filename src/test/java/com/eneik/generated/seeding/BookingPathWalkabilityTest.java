package com.eneik.generated.seeding;

import com.eneik.generated.model.AvailabilitySlot;
import com.eneik.generated.model.MasterProfile;
import com.eneik.generated.repository.AvailabilitySlotRepository;
import com.eneik.generated.repository.MasterProfileRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@io.zonky.test.db.AutoConfigureEmbeddedDatabase(type = io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
public class BookingPathWalkabilityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MasterProfileRepository masterProfileRepository;

    @Autowired
    private AvailabilitySlotRepository availabilitySlotRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Given seeded instance, When tester walks booking path, Then all links/endpoints are walkable")
    void testSeededBookingPathWalkability() throws Exception {
        // Step 1: Verify seeded masters and availability slots exist
        List<MasterProfile> masters = masterProfileRepository.findAll();
        assertThat(masters).isNotEmpty();
        MasterProfile master = masters.get(0);

        List<AvailabilitySlot> slots = availabilitySlotRepository.findByMasterId(master.getId());
        assertThat(slots).isNotEmpty();

        // Step 2: Propose booking slot via Voice Bridge endpoint
        String proposePayload = String.format("""
                {
                    "masterId": "%d",
                    "serviceName": "Haircut",
                    "slotTime": "%s"
                }
                """, master.getId(), slots.get(0).getStartTime().toString());

        MvcResult proposeResult = mockMvc.perform(post("/api/voice/bridge/propose-slot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(proposePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").exists())
                .andExpect(jsonPath("$.status").exists())
                .andReturn();

        JsonNode proposeJson = objectMapper.readTree(proposeResult.getResponse().getContentAsString());
        String bookingId = proposeJson.get("bookingId").asText();
        assertThat(bookingId).isNotBlank();

        // Step 3: Confirm booking via Voice Bridge confirm endpoint
        String confirmPayload = String.format("""
                {
                    "bookingId": "%s",
                    "voiceCommand": "ki, dadastureba"
                }
                """, bookingId);

        mockMvc.perform(post("/api/voice/bridge/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(confirmPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(bookingId))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        // Step 4: Record telemetry booking event for conversion metrics
        String telemetryPayload = String.format("""
                {
                    "bookingId": "%s",
                    "status": "CONFIRMED",
                    "details": "Georgian master booking confirmed"
                }
                """, bookingId);

        mockMvc.perform(post("/api/v1/telemetry/booking-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(telemetryPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metricName").value("gqm_conversion_count"));

        // Step 5: Query telemetry metrics to verify accurate conversion count
        mockMvc.perform(get("/api/v1/telemetry/metrics")
                        .param("name", "gqm_conversion_count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].metricName").value("gqm_conversion_count"))
                .andExpect(jsonPath("$[0].metricValue").exists());
    }
}
