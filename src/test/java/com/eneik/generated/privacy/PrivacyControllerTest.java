package com.eneik.generated.privacy;

import com.eneik.generated.privacy.controller.PrivacyController;
import com.eneik.generated.privacy.dto.PrivacyErasureRequest;
import com.eneik.generated.privacy.dto.PrivacyExportRequest;
import com.eneik.generated.privacy.model.UserDataRecord;
import com.eneik.generated.privacy.service.PrivacyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class PrivacyControllerTest {

    private final Instant fixedInstant = Instant.parse("2026-10-06T12:00:00Z");
    private final Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));

    private PrivacyService privacyService;
    private PrivacyController privacyController;

    @BeforeEach
    void setUp() {
        privacyService = new PrivacyService(fixedClock);
        privacyController = new PrivacyController(privacyService, fixedClock);
    }

    @Test
    void testCreateDataExportUnauthorizedWhenAuthHeaderMissing() {
        ResponseEntity<?> response = privacyController.createDataExport(null, null, new PrivacyExportRequest("JSON", true, null));
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testCreateDataExportSuccessWithValidAuth() {
        String userId = "user-test-100";
        privacyService.registerUserData(new UserDataRecord(
                userId, "+995555778899", "fb-100", "Luka Kapanadze", "KUTAISI", fixedInstant
        ));

        ResponseEntity<?> response = privacyController.createDataExport("Bearer " + userId, "req-1", new PrivacyExportRequest("JSON", true, null));
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testCreateDataErasureBadRequestWhenConfirmationMissing() {
        String userId = "user-test-200";
        ResponseEntity<?> response = privacyController.createDataErasure("Bearer " + userId, "req-2", new PrivacyErasureRequest(false, "ALL_DATA", "Reason"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateDataErasureSuccessWhenConfirmed() {
        String userId = "user-test-300";
        privacyService.registerUserData(new UserDataRecord(
                userId, "+995555001122", "fb-300", "Eka Shengelia", "TBILISI", fixedInstant
        ));

        ResponseEntity<?> response = privacyController.createDataErasure("Bearer " + userId, "req-3", new PrivacyErasureRequest(true, "ALL_DATA", "Close account"));
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        // Verify data was anonymized
        UserDataRecord record = privacyService.getUserData(userId).orElseThrow();
        assertTrue(record.isErased());
        assertEquals("[DELETED]", record.getPhone());
    }
}
