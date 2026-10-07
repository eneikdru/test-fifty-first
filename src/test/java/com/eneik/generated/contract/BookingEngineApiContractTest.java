package com.eneik.generated.contract;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BookingEngineApiContractTest {

    private static final Path CONTRACT_PATH = Paths.get("docs/contracts/booking-engine.openapi.yaml");

    @Test
    @DisplayName("Verify Booking Engine OpenAPI contract file exists and is valid OpenAPI 3.0.3")
    void testContractFileExistsAndIsValidYaml() throws Exception {
        assertTrue(Files.exists(CONTRACT_PATH), "Contract file docs/contracts/booking-engine.openapi.yaml must exist");

        Yaml yaml = new Yaml();
        Map<String, Object> openApiMap;
        try (InputStream is = Files.newInputStream(CONTRACT_PATH)) {
            openApiMap = yaml.load(is);
        }

        assertNotNull(openApiMap, "Parsed YAML must not be null");
        assertEquals("3.0.3", openApiMap.get("openapi"), "OpenAPI version should be 3.0.3");

        Map<String, Object> paths = (Map<String, Object>) openApiMap.get("paths");
        assertNotNull(paths, "paths block must exist");
        assertTrue(paths.containsKey("/api/v1/bookings/hold"), "Hold endpoint must exist");
        assertTrue(paths.containsKey("/api/v1/bookings/confirm"), "Confirm endpoint must exist");
        assertTrue(paths.containsKey("/api/v1/mcp/confirm"), "MCP confirm endpoint must exist");
        assertTrue(paths.containsKey("/api/v1/mcp/tools/call"), "MCP tool call endpoint must exist");

        Map<String, Object> components = (Map<String, Object>) openApiMap.get("components");
        assertNotNull(components, "components block must exist");

        Map<String, Object> schemas = (Map<String, Object>) components.get("schemas");
        assertNotNull(schemas, "schemas block must exist");
        assertTrue(schemas.containsKey("HoldRequest"), "HoldRequest schema must exist");
        assertTrue(schemas.containsKey("BookingDto"), "BookingDto schema must exist");
        assertTrue(schemas.containsKey("McpToolCallRequest"), "McpToolCallRequest schema must exist");
    }
}
