package com.eneik.generated.controller;

import com.eneik.generated.model.BookingEntity;
import com.eneik.generated.service.BookingEngineService;
import com.eneik.generated.service.BookingEngineService.SlotUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mcp")
public class McpBookingController {

    private final BookingEngineService bookingEngineService;
    private final Clock clock;

    @org.springframework.beans.factory.annotation.Autowired
    public McpBookingController(BookingEngineService bookingEngineService) {
        this(bookingEngineService, Clock.systemUTC());
    }

    public McpBookingController(BookingEngineService bookingEngineService, Clock clock) {
        this.bookingEngineService = bookingEngineService;
        this.clock = clock != null ? clock : Clock.systemUTC();
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> mcpConfirm(@RequestBody Map<String, Object> body) {
        String bookingId = null;
        if (body != null) {
            if (body.get("bookingId") != null) {
                bookingId = body.get("bookingId").toString();
            } else if (body.get("params") instanceof Map<?, ?> params) {
                Object bId = params.get("bookingId");
                if (bId != null) {
                    bookingId = bId.toString();
                }
            }
        }

        if (bookingId == null || bookingId.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", "BAD_REQUEST", "message", "bookingId is required in MCP confirmation payload", "timestamp", OffsetDateTime.now(clock).toString()));
        }

        try {
            BookingEntity booking = bookingEngineService.confirmBooking(bookingId);
            return ResponseEntity.ok(booking);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("code", "CONFIRMATION_CONFLICT", "message", e.getMessage(), "timestamp", OffsetDateTime.now(clock).toString()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("code", "NOT_FOUND", "message", e.getMessage(), "timestamp", OffsetDateTime.now(clock).toString()));
        }
    }

    @PostMapping("/tools/call")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, Object>> mcpToolCall(@RequestBody Map<String, Object> toolCallRequest) {
        if (toolCallRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("content", List.of(Map.of("type", "text", "text", "Request payload is empty")), "isError", true));
        }

        String toolName = (String) toolCallRequest.get("name");
        Map<String, Object> args = (Map<String, Object>) toolCallRequest.getOrDefault("arguments", Map.of());

        if ("confirm_booking".equalsIgnoreCase(toolName)) {
            String bookingId = (String) args.get("bookingId");
            if (bookingId == null || bookingId.isBlank()) {
                return ResponseEntity.ok(Map.of("content", List.of(Map.of("type", "text", "text", "Missing bookingId argument")), "isError", true));
            }
            try {
                BookingEntity booking = bookingEngineService.confirmBooking(bookingId);
                return ResponseEntity.ok(Map.of(
                        "content", List.of(Map.of("type", "text", "text", "Booking " + booking.getId() + " confirmed and slot locked")),
                        "isError", false,
                        "booking", booking
                ));
            } catch (Exception e) {
                return ResponseEntity.ok(Map.of(
                        "content", List.of(Map.of("type", "text", "text", "Confirmation failed: " + e.getMessage())),
                        "isError", true
                ));
            }
        } else if ("hold_slot".equalsIgnoreCase(toolName)) {
            Long slotId = args.get("slotId") != null ? Long.valueOf(args.get("slotId").toString()) : null;
            Long masterId = args.get("masterId") != null ? Long.valueOf(args.get("masterId").toString()) : null;
            String customerPhone = (String) args.getOrDefault("customerPhone", "");
            String serviceName = (String) args.getOrDefault("serviceName", "General Service");
            Double priceGel = args.get("priceGel") != null ? Double.valueOf(args.get("priceGel").toString()) : 0.0;

            if (slotId == null || masterId == null) {
                return ResponseEntity.ok(Map.of("content", List.of(Map.of("type", "text", "text", "Missing slotId or masterId arguments")), "isError", true));
            }

            try {
                BookingEntity booking = bookingEngineService.createHold(slotId, masterId, customerPhone, serviceName, priceGel);
                return ResponseEntity.ok(Map.of(
                        "content", List.of(Map.of("type", "text", "text", "Slot " + slotId + " held with bookingId: " + booking.getId())),
                        "isError", false,
                        "booking", booking
                ));
            } catch (SlotUnavailableException e) {
                return ResponseEntity.ok(Map.of(
                        "content", List.of(Map.of("type", "text", "text", "Hold failed: " + e.getMessage())),
                        "isError", true
                ));
            } catch (Exception e) {
                return ResponseEntity.ok(Map.of(
                        "content", List.of(Map.of("type", "text", "text", "Error: " + e.getMessage())),
                        "isError", true
                ));
            }
        }

        return ResponseEntity.ok(Map.of("content", List.of(Map.of("type", "text", "text", "Unknown MCP tool: " + toolName)), "isError", true));
    }
}
