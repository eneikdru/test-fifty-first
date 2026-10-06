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

class LedgerUpdatesContractTest {

    private static final Path CONTRACT_PATH = Paths.get("docs/contracts/ledger-updates.openapi.yaml");

    @Test
    @DisplayName("Verify OpenAPI spec file exists and is valid YAML")
    void testContractFileExistsAndIsValidYaml() throws Exception {
        assertTrue(Files.exists(CONTRACT_PATH), "Contract file docs/contracts/ledger-updates.openapi.yaml must exist");

        Yaml yaml = new Yaml();
        Map<String, Object> openApiMap;
        try (InputStream is = Files.newInputStream(CONTRACT_PATH)) {
            openApiMap = yaml.load(is);
        }

        assertNotNull(openApiMap, "Parsed YAML must not be null");
        assertEquals("3.0.3", openApiMap.get("openapi"), "OpenAPI version should be 3.0.3");

        @SuppressWarnings("unchecked")
        Map<String, Object> info = (Map<String, Object>) openApiMap.get("info");
        assertNotNull(info, "info block must exist");
        assertEquals("Ledger Updates API Contract", info.get("title"));
    }

    @Test
    @DisplayName("Verify payment status allows 'paid' and 'unpaid' states")
    @SuppressWarnings("unchecked")
    void testPaymentStatusAllowsPaidAndUnpaidStates() throws Exception {
        Yaml yaml = new Yaml();
        Map<String, Object> openApiMap;
        try (InputStream is = Files.newInputStream(CONTRACT_PATH)) {
            openApiMap = yaml.load(is);
        }

        Map<String, Object> components = (Map<String, Object>) openApiMap.get("components");
        assertNotNull(components, "components block must exist");

        Map<String, Object> schemas = (Map<String, Object>) components.get("schemas");
        assertNotNull(schemas, "schemas block must exist");

        Map<String, Object> paymentStatusSchema = (Map<String, Object>) schemas.get("PaymentStatus");
        assertNotNull(paymentStatusSchema, "PaymentStatus schema must exist");

        List<String> enumValues = (List<String>) paymentStatusSchema.get("enum");
        assertNotNull(enumValues, "PaymentStatus enum values must exist");
        assertTrue(enumValues.contains("paid"), "PaymentStatus must contain 'paid'");
        assertTrue(enumValues.contains("unpaid"), "PaymentStatus must contain 'unpaid'");
        assertEquals(2, enumValues.size(), "PaymentStatus enum should have exactly 'paid' and 'unpaid' states");
    }

    @Test
    @DisplayName("Verify querying bookings includes payment status")
    @SuppressWarnings("unchecked")
    void testQueryBookingsIncludesPaymentStatus() throws Exception {
        Yaml yaml = new Yaml();
        Map<String, Object> openApiMap;
        try (InputStream is = Files.newInputStream(CONTRACT_PATH)) {
            openApiMap = yaml.load(is);
        }

        Map<String, Object> paths = (Map<String, Object>) openApiMap.get("paths");
        assertNotNull(paths, "paths block must exist");

        assertTrue(paths.containsKey("/api/v1/ledger/bookings"), "GET /api/v1/ledger/bookings path must exist");
        Map<String, Object> bookingsPath = (Map<String, Object>) paths.get("/api/v1/ledger/bookings");
        Map<String, Object> getOperation = (Map<String, Object>) bookingsPath.get("get");
        assertNotNull(getOperation, "GET operation for /api/v1/ledger/bookings must exist");

        Map<String, Object> components = (Map<String, Object>) openApiMap.get("components");
        Map<String, Object> schemas = (Map<String, Object>) components.get("schemas");

        Map<String, Object> bookingLedgerItem = (Map<String, Object>) schemas.get("BookingLedgerItem");
        assertNotNull(bookingLedgerItem, "BookingLedgerItem schema must exist");

        List<String> requiredFields = (List<String>) bookingLedgerItem.get("required");
        assertNotNull(requiredFields, "BookingLedgerItem required fields must exist");
        assertTrue(requiredFields.contains("paymentStatus"), "BookingLedgerItem must require paymentStatus");

        Map<String, Object> properties = (Map<String, Object>) bookingLedgerItem.get("properties");
        assertNotNull(properties, "BookingLedgerItem properties must exist");
        assertTrue(properties.containsKey("paymentStatus"), "BookingLedgerItem must contain paymentStatus property");
    }

    @Test
    @DisplayName("Verify payment status update endpoint defines PUT and PATCH with error responses")
    @SuppressWarnings("unchecked")
    void testPaymentStatusUpdateEndpointDefined() throws Exception {
        Yaml yaml = new Yaml();
        Map<String, Object> openApiMap;
        try (InputStream is = Files.newInputStream(CONTRACT_PATH)) {
            openApiMap = yaml.load(is);
        }

        Map<String, Object> paths = (Map<String, Object>) openApiMap.get("paths");
        assertTrue(paths.containsKey("/api/v1/ledger/bookings/{bookingId}/payment-status"),
                "Path /api/v1/ledger/bookings/{bookingId}/payment-status must exist");

        Map<String, Object> updatePath = (Map<String, Object>) paths.get("/api/v1/ledger/bookings/{bookingId}/payment-status");
        assertTrue(updatePath.containsKey("put"), "PUT operation must be defined for payment status update");
        assertTrue(updatePath.containsKey("patch"), "PATCH operation must be defined for payment status update");

        Map<String, Object> putOp = (Map<String, Object>) updatePath.get("put");
        Map<String, Object> responses = (Map<String, Object>) putOp.get("responses");
        assertNotNull(responses, "Responses block must exist for PUT operation");
        assertTrue(responses.containsKey("200"), "200 response must exist");
        assertTrue(responses.containsKey("400"), "400 response must exist");
        assertTrue(responses.containsKey("404"), "404 response must exist");
        assertTrue(responses.containsKey("409"), "409 response must exist");
    }
}
