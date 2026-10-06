package com.eneik.generated.controller;

import com.eneik.generated.analytics.SystemMetric;
import com.eneik.generated.service.TelemetryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/telemetry")
public class TelemetryController {

    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<List<SystemMetric>> getAllMetrics() {
        return ResponseEntity.ok(telemetryService.getAllMetrics());
    }

    @GetMapping("/gqm-conversion")
    public ResponseEntity<Map<String, Object>> getGqmConversion() {
        double value = telemetryService.getGqmBookingConversionValue();
        return ResponseEntity.ok(Map.of(
                "metricName", TelemetryService.GQM_CONVERSION_METRIC_NAME,
                "conversionCount", value
        ));
    }
}
