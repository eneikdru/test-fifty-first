package com.eneik.generated.booking;

import com.eneik.generated.model.AvailabilitySlot;
import com.eneik.generated.model.BookingEntity;
import com.eneik.generated.model.MasterProfile;
import com.eneik.generated.repository.AvailabilitySlotRepository;
import com.eneik.generated.repository.BookingRepository;
import com.eneik.generated.repository.MasterProfileRepository;
import com.eneik.generated.service.BookingEngineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class McpBookingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingEngineService bookingEngineService;

    @Autowired
    private MasterProfileRepository masterRepository;

    @Autowired
    private AvailabilitySlotRepository slotRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private Long masterId;
    private Long slotId;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();

        List<MasterProfile> masters = masterRepository.findAll();
        MasterProfile master;
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        if (masters.isEmpty()) {
            master = masterRepository.save(new MasterProfile("Giga Beridze", "Tbilisi", "+995599123456", null, "Barber", now));
        } else {
            master = masters.get(0);
        }
        this.masterId = master.getId();

        AvailabilitySlot slot = new AvailabilitySlot(masterId, now.plusDays(2), now.plusDays(2).plusHours(1), "AVAILABLE", now);
        slot = slotRepository.save(slot);
        this.slotId = slot.getId();
    }

    @Test
    @DisplayName("Given an MCP confirmation, booking is finalized and slot locked")
    void testMcpConfirmEndpointFinalizesBookingAndLocksSlot() throws Exception {
        BookingEntity holdBooking = bookingEngineService.createHold(
                slotId, masterId, "+995599123456", "Haircut", 35.0
        );
        assertEquals("HELD", holdBooking.getStatus());

        String jsonPayload = """
                {
                    "bookingId": "%s",
                    "notes": "Confirmed via MCP agent"
                }
                """.formatted(holdBooking.getId());

        mockMvc.perform(post("/api/v1/mcp/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(holdBooking.getId())))
                .andExpect(jsonPath("$.status", is("CONFIRMED")));

        BookingEntity confirmedBooking = bookingRepository.findById(holdBooking.getId()).orElseThrow();
        assertEquals("CONFIRMED", confirmedBooking.getStatus(), "Booking status must be CONFIRMED");

        AvailabilitySlot lockedSlot = slotRepository.findById(slotId).orElseThrow();
        assertEquals("LOCKED", lockedSlot.getStatus(), "Slot status must be LOCKED after confirmation");
    }

    @Test
    @DisplayName("Given an MCP tool call 'confirm_booking', booking is finalized and slot locked")
    void testMcpToolCallConfirmBooking() throws Exception {
        BookingEntity holdBooking = bookingEngineService.createHold(
                slotId, masterId, "+995599123456", "Haircut", 35.0
        );

        String jsonPayload = """
                {
                    "name": "confirm_booking",
                    "arguments": {
                        "bookingId": "%s"
                    }
                }
                """.formatted(holdBooking.getId());

        mockMvc.perform(post("/api/v1/mcp/tools/call")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isError", is(false)))
                .andExpect(jsonPath("$.content[0].text", containsString(holdBooking.getId())));

        AvailabilitySlot lockedSlot = slotRepository.findById(slotId).orElseThrow();
        assertEquals("LOCKED", lockedSlot.getStatus(), "Slot status must be locked via MCP tool call");
    }
}
