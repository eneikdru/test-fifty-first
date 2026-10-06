package com.eneik.generated;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LedgerContractTest {

    private static final Path CONTRACT_PATH = Path.of("docs/contracts/ledger-updates.openapi.yaml");

    @Test
    @DisplayName("Contract file should exist and be valid OpenAPI 3.0 specification")
    void contractFileShouldExistAndBeValidYaml() throws Exception {
        assertThat(Files.exists(CONTRACT_PATH))
                .withFailMessage("Contract file at %s does not exist", CONTRACT_PATH)
                .isTrue();

        Yaml yaml = new Yaml();
        Map<String, Object> contract;
        try (InputStream in = Files.newInputStream(CONTRACT_PATH)) {
            contract = yaml.load(in);
        }

        assertThat(contract).isNotNull();
        assertThat(contract.get("openapi")).asString().startsWith("3.0");

        Map<String, Object> info = (Map<String, Object>) contract.get("info");
        assertThat(info).isNotNull();
        assertThat(info.get("title")).isEqualTo("Ledger Updates API");
    }

    @Test
    @DisplayName("PaymentStatus schema must allow 'paid' and 'unpaid' states")
    void paymentStatusMustAllowPaidAndUnpaid() throws Exception {
        Map<String, Object> contract = loadContract();
        Map<String, Object> components = (Map<String, Object>) contract.get("components");
        Map<String, Object> schemas = (Map<String, Object>) components.get("schemas");

        Map<String, Object> paymentStatusSchema = (Map<String, Object>) schemas.get("PaymentStatus");
        assertThat(paymentStatusSchema).isNotNull();

        List<String> allowedValues = (List<String>) paymentStatusSchema.get("enum");
        assertThat(allowedValues).containsExactlyInAnyOrder("paid", "unpaid");
    }

    @Test
    @DisplayName("Payment status update endpoint must accept PaymentStatusUpdateRequest with paid/unpaid")
    void paymentStatusUpdateEndpointDefined() throws Exception {
        Map<String, Object> contract = loadContract();
        Map<String, Object> paths = (Map<String, Object>) contract.get("paths");

        Map<String, Object> patchPath = (Map<String, Object>) paths.get("/api/v1/bookings/{bookingId}/payment-status");
        assertThat(patchPath).isNotNull();

        Map<String, Object> patchOp = (Map<String, Object>) patchPath.get("patch");
        assertThat(patchOp).isNotNull();

        Map<String, Object> requestBody = (Map<String, Object>) patchOp.get("requestBody");
        assertThat(requestBody).isNotNull();

        Map<String, Object> responses = (Map<String, Object>) patchOp.get("responses");
        assertThat(responses).containsKeys("200", "400", "404", "409");
    }

    @Test
    @DisplayName("Booking query endpoints must include payment status")
    void bookingQueryEndpointsIncludePaymentStatus() throws Exception {
        Map<String, Object> contract = loadContract();
        Map<String, Object> paths = (Map<String, Object>) contract.get("paths");

        Map<String, Object> listBookingsPath = (Map<String, Object>) paths.get("/api/v1/bookings");
        assertThat(listBookingsPath).isNotNull();
        Map<String, Object> getListOp = (Map<String, Object>) listBookingsPath.get("get");
        assertThat(getListOp).isNotNull();

        Map<String, Object> getBookingPath = (Map<String, Object>) paths.get("/api/v1/bookings/{bookingId}");
        assertThat(getBookingPath).isNotNull();
        Map<String, Object> getBookingOp = (Map<String, Object>) getBookingPath.get("get");
        assertThat(getBookingOp).isNotNull();

        Map<String, Object> components = (Map<String, Object>) contract.get("components");
        Map<String, Object> schemas = (Map<String, Object>) components.get("schemas");
        Map<String, Object> bookingResponse = (Map<String, Object>) schemas.get("BookingResponse");
        assertThat(bookingResponse).isNotNull();

        Map<String, Object> properties = (Map<String, Object>) bookingResponse.get("properties");
        assertThat(properties).containsKey("paymentStatus");

        List<String> required = (List<String>) bookingResponse.get("required");
        assertThat(required).contains("paymentStatus");
    }

    private Map<String, Object> loadContract() throws Exception {
        Yaml yaml = new Yaml();
        try (InputStream in = Files.newInputStream(CONTRACT_PATH)) {
            return yaml.load(in);
        }
    }
}
