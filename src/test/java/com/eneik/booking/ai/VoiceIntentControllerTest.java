package com.eneik.booking.ai;

import com.eneik.booking.ai.controller.VoiceIntentController;
import com.eneik.booking.ai.model.BookingIntentRequest;
import com.eneik.booking.ai.model.BookingIntentResponse;
import com.eneik.booking.ai.service.GeorgianIntentParserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VoiceIntentController.class)
class VoiceIntentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GeorgianIntentParserService intentParserService;

    @Test
    void parseIntent_returnsOk() throws Exception {
        BookingIntentRequest request = new BookingIntentRequest("მინდა თმის შეჭრა ხვალ 15:00 საათზე");
        BookingIntentResponse mockResponse = new BookingIntentResponse(
                "მინდა თმის შეჭრა ხვალ 15:00 საათზე",
                "HAIRCUT",
                LocalDateTime.of(2026, 10, 7, 15, 0),
                "Tbilisi",
                0.95,
                false,
                "SUCCESS"
        );

        given(intentParserService.parseIntent(any())).willReturn(mockResponse);

        mockMvc.perform(post("/api/ai/intent/parse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.extractedService").value("HAIRCUT"))
                .andExpect(jsonPath("$.lowConfidence").value(false))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}
