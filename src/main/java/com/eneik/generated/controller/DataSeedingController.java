package com.eneik.generated.controller;

import com.eneik.generated.domain.MasterProfile;
import com.eneik.generated.service.DataSeedingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seed")
public class DataSeedingController {

    private final DataSeedingService dataSeedingService;

    public DataSeedingController(DataSeedingService dataSeedingService) {
        this.dataSeedingService = dataSeedingService;
    }

    @PostMapping
    public ResponseEntity<List<MasterProfile>> seedData() {
        List<MasterProfile> profiles = dataSeedingService.seedRealisticGeorgianProfiles();
        return ResponseEntity.ok(profiles);
    }
}
