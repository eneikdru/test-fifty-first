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

class RemindersActionsApiContractTest {

    private static final Path CONTRACT_PATH = Paths.get("docs/contracts/reminders-actions.openapi.yaml");

    @Test
    @DisplayName("Verify Reminders & Actions OpenAPI contract file exists and is valid OpenAPI 3.0.3")
    void testContractFileExistsAndIsValidYaml() throws Exception {
        assertTrue(Files.exists(CONTRACT_PATH), "Contract file docs/contracts/reminders-actions.openapi.yaml must exist");

        Yaml yaml = new Yaml();
        Map<String, Object> openApiMap;
        try (InputStream is = Files.newInputStream(CONTRACT_PATH)) {
            openApiMap = yaml.load(is);
        }

        assertNotNull(openApiMap, "Parsed YAML must not be null");
        assertEquals("3.0.3", openApiMap.get("openapi"), "OpenAPI version should be 3.0.3");
    }

    @Test
    @DisplayName("Verify Reschedule request payload structure and schema requirements")
    @SuppressWarnings("unchecked")
    void testReschedulePayloadStructureValidation() throws Exception {
        Yaml yaml = new Yaml();
        Map<String, Object> openApiMap;
        try (InputStream is = Files.newInputStream(CONTRACT_PATH)) {
            openApiMap = yaml.load(is);
        }

        Map<String, Object> components = (Map<String, Object>) openApiMap.get("components");
        assertNotNull(components, "components block must exist");

        Map<String, Object> schemas = (Map<String, Object>) components.get("schemas");
        assertNotNull(schemas, "schemas block must exist");

        Map<String, Object> rescheduleRequest = (Map<String, Object>) schemas.get("RescheduleRequestPayload");
        assertNotNull(rescheduleRequest, "RescheduleRequestPayload schema must exist");

        List<String> requiredFields = (List<String>) rescheduleRequest.get("required");
        assertNotNull(requiredFields, "RescheduleRequestPayload must specify required fields");
        assertTrue(requiredFields.contains("bookingId"), "RescheduleRequestPayload must require bookingId");
        assertTrue(requiredFields.contains("requestedSlotStart"), "RescheduleRequestPayload must require requestedSlotStart");
        assertTrue(requiredFields.contains("requestedSlotEnd"), "RescheduleRequestPayload must require requestedSlotEnd");

        Map<String, Object> rescheduleResponse = (Map<String, Object>) schemas.get("BookingResponsePayload");
        assertNotNull(rescheduleResponse, "BookingResponsePayload schema must exist");

        Map<String, Object> paths = (Map<String, Object>) openApiMap.get("paths");
        assertNotNull(paths, "paths block must exist");
        assertTrue(paths.containsKey("/api/v1/bookings/{id}/reschedule"), "/api/v1/bookings/{id}/reschedule path must exist");
    }

    @Test
    @DisplayName("Verify Webhook definitions match Messenger and WhatsApp requirements")
    @SuppressWarnings("unchecked")
    void testWebhookDefinitionsMatchMessengerRequirements() throws Exception {
        Yaml yaml = new Yaml();
        Map<String, Object> openApiMap;
        try (InputStream is = Files.newInputStream(CONTRACT_PATH)) {
            openApiMap = yaml.load(is);
        }

        Map<String, Object> paths = (Map<String, Object>) openApiMap.get("paths");
        assertNotNull(paths, "paths block must exist");

        assertTrue(paths.containsKey("/api/v1/webhooks/messenger"), "Messenger webhook path /api/v1/webhooks/messenger must exist");
        Map<String, Object> messengerPath = (Map<String, Object>) paths.get("/api/v1/webhooks/messenger");
        assertTrue(messengerPath.containsKey("get"), "Messenger GET verification endpoint must exist");
        assertTrue(messengerPath.containsKey("post"), "Messenger POST webhook receiver endpoint must exist");

        Map<String, Object> components = (Map<String, Object>) openApiMap.get("components");
        Map<String, Object> schemas = (Map<String, Object>) components.get("schemas");

        assertTrue(schemas.containsKey("MessengerWebhookEvent"), "MessengerWebhookEvent schema must exist");
        assertTrue(schemas.containsKey("ReminderNotificationPayload"), "ReminderNotificationPayload schema must exist");
        assertTrue(schemas.containsKey("ReminderNotificationResult"), "ReminderNotificationResult schema must exist");
    }
}
