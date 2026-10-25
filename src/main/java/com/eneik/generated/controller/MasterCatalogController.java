package com.eneik.generated.controller;

import com.eneik.generated.dto.MasterServiceRequest;
import com.eneik.generated.dto.MasterServiceResponse;
import com.eneik.generated.service.MasterCatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/masters/{masterId}/services")
public class MasterCatalogController {

    private final MasterCatalogService catalogService;

    public MasterCatalogController(MasterCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public ResponseEntity<List<MasterServiceResponse>> getServices(
            @PathVariable Long masterId,
            @RequestHeader(value = "Accept-Language", required = false, defaultValue = "en") String acceptLanguage) {
        return ResponseEntity.ok(catalogService.getServices(masterId, acceptLanguage));
    }

    @PostMapping
    public ResponseEntity<?> addService(
            @PathVariable Long masterId,
            @RequestBody MasterServiceRequest request) {
        try {
            MasterServiceResponse response = catalogService.addService(masterId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
