package com.eneik.generated.controller;

import com.eneik.generated.domain.AvailabilitySlot;
import com.eneik.generated.domain.AvailabilitySlotRepository;
import com.eneik.generated.domain.MasterProfile;
import com.eneik.generated.domain.MasterProfileRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/masters")
public class MasterProfileController {

    private final MasterProfileRepository masterProfileRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;

    public MasterProfileController(MasterProfileRepository masterProfileRepository,
                                  AvailabilitySlotRepository availabilitySlotRepository) {
        this.masterProfileRepository = masterProfileRepository;
        this.availabilitySlotRepository = availabilitySlotRepository;
    }

    @GetMapping
    public ResponseEntity<List<MasterProfile>> getMasters(@RequestParam(required = false) String city) {
        if (city != null && !city.isBlank()) {
            return ResponseEntity.ok(masterProfileRepository.findByCity(city));
        }
        return ResponseEntity.ok(masterProfileRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MasterProfile> getMasterById(@PathVariable String id) {
        return masterProfileRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<List<AvailabilitySlot>> getMasterSlots(@PathVariable String id) {
        return ResponseEntity.ok(availabilitySlotRepository.findByMasterIdAndIsBookedFalse(id));
    }
}
