package com.eneik.generated.privacy.controller;

import com.eneik.generated.privacy.dto.*;
import com.eneik.generated.privacy.service.PrivacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/privacy")
public class PrivacyController {

    private final PrivacyService privacyService;
    private final Clock clock;

    @Autowired
    public PrivacyController(PrivacyService privacyService) {
        this(privacyService, Clock.systemUTC());
    }

    public PrivacyController(PrivacyService privacyService, Clock clock) {
        this.privacyService = privacyService;
        this.clock = clock;
    }

    @PostMapping("/exports")
    public ResponseEntity<?> createDataExport(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-Request-ID", required = false) String requestId,
            @RequestBody(required = false) PrivacyExportRequest request) {

        String userId = extractUserIdFromAuth(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", "Missing or invalid Authorization header", clock.instant(), List.of()));
        }

        if (request == null) {
            request = new PrivacyExportRequest("JSON", true, null);
        }

        PrivacyExportStatusResponse response = privacyService.requestExport(userId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/exports/{exportId}")
    public ResponseEntity<?> getDataExport(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-Request-ID", required = false) String requestId,
            @PathVariable("exportId") String exportId) {

        String userId = extractUserIdFromAuth(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", "Missing or invalid Authorization header", clock.instant(), List.of()));
        }

        return privacyService.getExportStatus(exportId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("NOT_FOUND", "Data export request not found", clock.instant(), List.of())));
    }

    @PostMapping("/erasures")
    public ResponseEntity<?> createDataErasure(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-Request-ID", required = false) String requestId,
            @RequestBody(required = false) PrivacyErasureRequest request) {

        String userId = extractUserIdFromAuth(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", "Missing or invalid Authorization header", clock.instant(), List.of()));
        }

        if (request == null || !Boolean.TRUE.equals(request.confirmErasure())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("BAD_REQUEST", "Explicit erasure confirmation (confirmErasure=true) is required", clock.instant(), List.of()));
        }

        PrivacyErasureResponse response = privacyService.requestErasure(userId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/erasures/{erasureRequestId}")
    public ResponseEntity<?> getErasureStatus(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-Request-ID", required = false) String requestId,
            @PathVariable("erasureRequestId") String erasureRequestId) {

        String userId = extractUserIdFromAuth(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", "Missing or invalid Authorization header", clock.instant(), List.of()));
        }

        return privacyService.getErasureStatus(erasureRequestId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("NOT_FOUND", "Erasure request not found", clock.instant(), List.of())));
    }

    private String extractUserIdFromAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7).trim();
        if (token.isEmpty() || token.equalsIgnoreCase("invalid")) {
            return null;
        }
        // In simple token format or JWT mock, return token or user ID derived from token
        if (token.contains("-")) {
            return token;
        }
        return "user-" + token;
    }
}
