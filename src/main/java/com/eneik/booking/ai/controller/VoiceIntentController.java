package com.eneik.booking.ai.controller;

import com.eneik.booking.ai.model.BookingIntentRequest;
import com.eneik.booking.ai.model.BookingIntentResponse;
import com.eneik.booking.ai.service.GeorgianIntentParserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/intent")
public class VoiceIntentController {

    private final GeorgianIntentParserService intentParserService;

    public VoiceIntentController(GeorgianIntentParserService intentParserService) {
        this.intentParserService = intentParserService;
    }

    @PostMapping("/parse")
    public ResponseEntity<BookingIntentResponse> parseIntent(@RequestBody BookingIntentRequest request) {
        BookingIntentResponse response = intentParserService.parseIntent(request);
        return ResponseEntity.ok(response);
    }
}
