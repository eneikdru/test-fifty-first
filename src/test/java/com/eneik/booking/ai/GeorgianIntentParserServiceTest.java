package com.eneik.booking.ai;

import com.eneik.booking.ai.model.BookingIntentRequest;
import com.eneik.booking.ai.model.BookingIntentResponse;
import com.eneik.booking.ai.service.GeorgianIntentParserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class GeorgianIntentParserServiceTest {

    private GeorgianIntentParserService parserService;
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        // Fixed instant: 2026-10-06T10:00:00Z
        Instant fixedInstant = Instant.parse("2026-10-06T10:00:00Z");
        fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));
        parserService = new GeorgianIntentParserService(fixedClock);
    }

    @Test
    void givenGeorgianAudioText_whenProcessed_thenServiceAndTimeIntentAreExtracted() {
        // "მინდა თმის შეჭრა ხვალ 15:00 საათზე თბილისში" (I want haircut tomorrow at 15:00 in Tbilisi)
        BookingIntentRequest request = new BookingIntentRequest("მინდა თმის შეჭრა ხვალ 15:00 საათზე თბილისში");

        BookingIntentResponse response = parserService.parseIntent(request);

        assertNotNull(response);
        assertEquals("HAIRCUT", response.getExtractedService());
        assertEquals(LocalDateTime.of(2026, 10, 7, 15, 0), response.getRequestedTime());
        assertEquals("Tbilisi", response.getLocation());
        assertFalse(response.isLowConfidence());
        assertTrue(response.getConfidenceScore() >= 0.65);
        assertEquals("SUCCESS", response.getStatus());
    }

    @Test
    void givenAmbiguousSpeech_whenParsed_thenLowConfidenceFlagIsRaised() {
        // "შეიძლება რაღაც დროის მერე" (Maybe after some time - ambiguous intent)
        BookingIntentRequest request = new BookingIntentRequest("შეიძლება რაღაც დროის მერე");

        BookingIntentResponse response = parserService.parseIntent(request);

        assertNotNull(response);
        assertNull(response.getExtractedService());
        assertNull(response.getRequestedTime());
        assertTrue(response.isLowConfidence());
        assertEquals("LOW_CONFIDENCE", response.getStatus());
        assertTrue(response.getConfidenceScore() < 0.65);
    }

    @Test
    void givenAmbiguousAudioPayload_whenProcessed_thenLowConfidenceAudioSimulated() {
        BookingIntentRequest request = new BookingIntentRequest("ambiguous_audio_bytes", null, "ka");

        BookingIntentResponse response = parserService.parseIntent(request);

        assertNotNull(response);
        assertTrue(response.isLowConfidence());
        assertEquals("LOW_CONFIDENCE", response.getStatus());
    }

    @Test
    void givenEmptyInput_whenParsed_thenDeterministicFallbackTriggered() {
        BookingIntentRequest request = new BookingIntentRequest(null);

        BookingIntentResponse response = parserService.parseIntent(request);

        assertNotNull(response);
        assertTrue(response.isLowConfidence());
        assertTrue(response.getStatus().startsWith("FALLBACK:"));
        assertEquals(0.0, response.getConfidenceScore());
    }
}
