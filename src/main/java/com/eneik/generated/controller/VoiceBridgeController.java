package com.eneik.generated.controller;

import com.eneik.generated.service.VoiceBookingService;
import com.eneik.generated.service.VoiceBookingService.VoiceBooking;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/voice/bridge")
public class VoiceBridgeController {

    private final VoiceBookingService bookingService;

    public VoiceBridgeController(VoiceBookingService bookingService) {
        this.bookingService = bookingService;
    }

    public static class ProposeSlotRequest {
        private String masterId;
        private String serviceName;
        private String slotTime;

        public ProposeSlotRequest() {}

        public ProposeSlotRequest(String masterId, String serviceName, String slotTime) {
            this.masterId = masterId;
            this.serviceName = serviceName;
            this.slotTime = slotTime;
        }

        public String getMasterId() {
            return masterId;
        }

        public void setMasterId(String masterId) {
            this.masterId = masterId;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getSlotTime() {
            return slotTime;
        }

        public void setSlotTime(String slotTime) {
            this.slotTime = slotTime;
        }
    }

    public static class ConfirmVoiceRequest {
        private String bookingId;
        private String voiceCommand;

        public ConfirmVoiceRequest() {}

        public ConfirmVoiceRequest(String bookingId, String voiceCommand) {
            this.bookingId = bookingId;
            this.voiceCommand = voiceCommand;
        }

        public String getBookingId() {
            return bookingId;
        }

        public void setBookingId(String bookingId) {
            this.bookingId = bookingId;
        }

        public String getVoiceCommand() {
            return voiceCommand;
        }

        public void setVoiceCommand(String voiceCommand) {
            this.voiceCommand = voiceCommand;
        }
    }

    @PostMapping("/propose-slot")
    public ResponseEntity<Map<String, Object>> proposeSlot(@RequestBody ProposeSlotRequest request) {
        VoiceBooking booking = bookingService.proposeSlot(
            request.getMasterId(),
            request.getServiceName(),
            request.getSlotTime()
        );

        Map<String, Object> response = Map.of(
            "bookingId", booking.getId(),
            "masterId", booking.getMasterId() != null ? booking.getMasterId() : "",
            "serviceName", booking.getServiceName() != null ? booking.getServiceName() : "",
            "slotTime", booking.getSlotTime() != null ? booking.getSlotTime() : "",
            "status", booking.getStatus().name(),
            "textResponse", booking.getTextResponse(),
            "audioBase64", booking.getAudioBase64(),
            "audioFormat", booking.getAudioFormat()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Object>> confirmBooking(@RequestBody ConfirmVoiceRequest request) {
        Optional<VoiceBooking> updated = bookingService.confirmBooking(request.getBookingId());
        if (updated.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Booking not found",
                "bookingId", request.getBookingId() != null ? request.getBookingId() : ""
            ));
        }

        VoiceBooking booking = updated.get();
        return ResponseEntity.ok(Map.of(
            "bookingId", booking.getId(),
            "status", booking.getStatus().name(),
            "message", "Booking confirmed and marked complete"
        ));
    }
}
