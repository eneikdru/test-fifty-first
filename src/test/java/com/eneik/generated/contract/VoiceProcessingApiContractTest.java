package com.eneik.generated.contract;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VoiceProcessingApiContractTest {

    private static final Path CONTRACT_PATH = Paths.get("docs/contracts/VoiceProcessing.openapi.yaml");

    @Test
    @DisplayName("Verify OpenAPI spec file exists and is valid YAML")
    void testContractFileExistsAndIsValidYaml() throws Exception {
        assertTrue(Files.exists(CONTRACT_PATH), "Contract file docs/contracts/VoiceProcessing.openapi.yaml must exist");

        Yaml yaml = new Yaml();
        Map<String, Object> openApiMap;
        try (InputStream is = Files.newInputStream(CONTRACT_PATH)) {
            openApiMap = yaml.load(is);
        }

        assertNotNull(openApiMap, "Parsed YAML must not be null");
        assertEquals("3.0.3", openApiMap.get("openapi"), "OpenAPI version should be 3.0.3");
    }

    @Test
    @DisplayName("Verify audio metadata is included in voice stream start payload")
    @SuppressWarnings("unchecked")
    void testAudioMetadataInStreamStartPayload() throws Exception {
        Yaml yaml = new Yaml();
        Map<String, Object> openApiMap;
        try (InputStream is = Files.newInputStream(CONTRACT_PATH)) {
            openApiMap = yaml.load(is);
        }

        Map<String, Object> components = (Map<String, Object>) openApiMap.get("components");
        assertNotNull(components, "components block must exist");

        Map<String, Object> schemas = (Map<String, Object>) components.get("schemas");
        assertNotNull(schemas, "schemas block must exist");

        Map<String, Object> audioMetadata = (Map<String, Object>) schemas.get("AudioMetadata");
        assertNotNull(audioMetadata, "AudioMetadata schema must exist");

        List<String> requiredFields = (List<String>) audioMetadata.get("required");
        assertNotNull(requiredFields, "AudioMetadata must have required fields");
        assertTrue(requiredFields.contains("sampleRate"), "AudioMetadata must require sampleRate");
        assertTrue(requiredFields.contains("encoding"), "AudioMetadata must require encoding");
        assertTrue(requiredFields.contains("languageCode"), "AudioMetadata must require languageCode");

        Map<String, Object> properties = (Map<String, Object>) audioMetadata.get("properties");
        assertNotNull(properties, "AudioMetadata must have properties");
        assertTrue(properties.containsKey("handsFreeMode"), "AudioMetadata must contain handsFreeMode property");

        // Verify endpoint /api/v1/voice/stream/start
        Map<String, Object> paths = (Map<String, Object>) openApiMap.get("paths");
        assertNotNull(paths, "paths block must exist");
        assertTrue(paths.containsKey("/api/v1/voice/stream/start"), "/api/v1/voice/stream/start path must exist");
    }

    @Test
    @DisplayName("Verify parsed intent returns payload conforming to UnifiedBookingDto")
    @SuppressWarnings("unchecked")
    void testParsedIntentConformsToUnifiedBookingDto() throws Exception {
        Yaml yaml = new Yaml();
        Map<String, Object> openApiMap;
        try (InputStream is = Files.newInputStream(CONTRACT_PATH)) {
            openApiMap = yaml.load(is);
        }

        Map<String, Object> components = (Map<String, Object>) openApiMap.get("components");
        Map<String, Object> schemas = (Map<String, Object>) components.get("schemas");

        // Verify UnifiedBookingDto schema exists with required fields
        Map<String, Object> bookingDto = (Map<String, Object>) schemas.get("UnifiedBookingDto");
        assertNotNull(bookingDto, "UnifiedBookingDto schema must exist");

        List<String> requiredBookingFields = (List<String>) bookingDto.get("required");
        assertNotNull(requiredBookingFields, "UnifiedBookingDto must have required fields");
        assertTrue(requiredBookingFields.contains("bookingId"), "UnifiedBookingDto requires bookingId");
        assertTrue(requiredBookingFields.contains("masterId"), "UnifiedBookingDto requires masterId");
        assertTrue(requiredBookingFields.contains("serviceId"), "UnifiedBookingDto requires serviceId");
        assertTrue(requiredBookingFields.contains("slot"), "UnifiedBookingDto requires slot");
        assertTrue(requiredBookingFields.contains("priceGEL"), "UnifiedBookingDto requires priceGEL");
        assertTrue(requiredBookingFields.contains("status"), "UnifiedBookingDto requires status");

        // Verify VoiceIntentResponse schema references UnifiedBookingDto
        Map<String, Object> intentResponse = (Map<String, Object>) schemas.get("VoiceIntentResponse");
        assertNotNull(intentResponse, "VoiceIntentResponse schema must exist");

        Map<String, Object> intentProperties = (Map<String, Object>) intentResponse.get("properties");
        Map<String, Object> bookingProperty = (Map<String, Object>) intentProperties.get("booking");
        assertNotNull(bookingProperty, "booking property must exist in VoiceIntentResponse");
        assertEquals("#/components/schemas/UnifiedBookingDto", bookingProperty.get("$ref"),
                "VoiceIntentResponse booking field must reference UnifiedBookingDto");
    }
}
