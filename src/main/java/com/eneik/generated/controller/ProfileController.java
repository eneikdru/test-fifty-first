package com.eneik.generated.controller;

import com.eneik.generated.dto.FacebookImportRequest;
import com.eneik.generated.dto.MasterProfileResponse;
import com.eneik.generated.dto.PhoneLoginRequest;
import com.eneik.generated.model.MasterProfile;
import com.eneik.generated.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/phone-login")
    public ResponseEntity<?> phoneLogin(@RequestBody PhoneLoginRequest request) {
        if (request == null || request.getPhone() == null || request.getPhone().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Phone number is required"));
        }

        try {
            MasterProfile master = profileService.provisionPhoneAccount(
                    request.getPhone(),
                    request.getName(),
                    request.getCity()
            );
            return ResponseEntity.ok(MasterProfileResponse.fromDomain(master));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/import/facebook")
    public ResponseEntity<?> importFacebook(@RequestBody FacebookImportRequest request) {
        if (request == null || request.getFacebookToken() == null || request.getFacebookToken().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Facebook access token is required"));
        }

        try {
            MasterProfile master = profileService.importFacebookProfile(
                    request.getFacebookToken(),
                    request.getFacebookPageId(),
                    request.getMasterId(),
                    request.getPhone()
            );
            return ResponseEntity.ok(MasterProfileResponse.fromDomain(master));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
