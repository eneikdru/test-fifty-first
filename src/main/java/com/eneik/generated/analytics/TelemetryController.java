package com.eneik.generated.analytics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/telemetry")
public class TelemetryController {

    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<List<SystemMetric>> getMetrics(@RequestParam(value = "name", required = false) String name) {
        return ResponseEntity.ok(telemetryService.getMetrics(name));
    }

    @PostMapping("/booking-events")
    public ResponseEntity<SystemMetric> processBookingEvent(@RequestBody BookingEventRequest request) {
        SystemMetric metric = telemetryService.processBookingEvent(
                request.getBookingId(),
                request.getStatus(),
                request.getDetails()
        );
        return ResponseEntity.ok(metric);
    }

    public static class BookingEventRequest {
        private String bookingId;
        private String status;
        private String details;

        public BookingEventRequest() {
        }

        public BookingEventRequest(String bookingId, String status, String details) {
            this.bookingId = bookingId;
            this.status = status;
            this.details = details;
        }

        public String getBookingId() {
            return bookingId;
        }

        public void setBookingId(String bookingId) {
            this.bookingId = bookingId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }
    }
}
