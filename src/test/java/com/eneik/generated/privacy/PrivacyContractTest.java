package com.eneik.generated.privacy;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrivacyContractTest {

    private static final Path CONTRACT_PATH = Path.of("docs/contracts/privacy-requests.openapi.yaml");

    @Test
    void testPrivacyContractFileExists() {
        File file = CONTRACT_PATH.toFile();
        assertTrue(file.exists(), "Privacy requests OpenAPI contract file should exist at docs/contracts/privacy-requests.openapi.yaml");
        assertTrue(file.isFile(), "Path should point to a file");
    }

    @Test
    void testExportEndpointAndStandardizedPayloadDefined() throws IOException {
        String content = Files.readString(CONTRACT_PATH);

        // Verify title
        assertTrue(content.contains("title: Privacy Requests API Contract"), "Contract should have expected title");

        // Verify export endpoint
        assertTrue(content.contains("/privacy/exports:"), "Contract should define /privacy/exports endpoint");
        assertTrue(content.contains("/privacy/exports/{exportId}:"), "Contract should define /privacy/exports/{exportId} endpoint");

        // Verify standardized JSON export payload structure
        assertTrue(content.contains("PrivacyExportPayload:"), "Contract should define PrivacyExportPayload schema");
        assertTrue(content.contains("userProfile:"), "Export payload must contain userProfile schema reference");
        assertTrue(content.contains("masterProfile:"), "Export payload must contain masterProfile schema reference");
        assertTrue(content.contains("bookings:"), "Export payload must contain bookings schema reference");
        assertTrue(content.contains("voiceRequests:"), "Export payload must contain voiceRequests schema reference");
        assertTrue(content.contains("messengerNotifications:"), "Export payload must contain messengerNotifications schema reference");
    }

    @Test
    void testErasureEndpointAndAuthenticationHeaderDefined() throws IOException {
        String content = Files.readString(CONTRACT_PATH);

        // Verify erasure endpoint
        assertTrue(content.contains("/privacy/erasures:"), "Contract should define /privacy/erasures endpoint");
        assertTrue(content.contains("/privacy/erasures/{erasureRequestId}:"), "Contract should define /privacy/erasures/{erasureRequestId} endpoint");

        // Verify authentication scheme and headers
        assertTrue(content.contains("BearerAuth:"), "Contract should define BearerAuth security scheme");
        assertTrue(content.contains("type: http"), "BearerAuth should be HTTP security scheme");
        assertTrue(content.contains("scheme: bearer"), "BearerAuth should use bearer scheme");
        assertTrue(content.contains("Authorization"), "Documented endpoints should reference Authorization header requirement");
        assertTrue(content.contains("PrivacyErasureRequest:"), "Contract should define PrivacyErasureRequest schema");
        assertTrue(content.contains("PrivacyErasureResponse:"), "Contract should define PrivacyErasureResponse schema");
    }
}
