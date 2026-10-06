package com.eneik.generated.messaging;

import com.eneik.generated.messaging.controller.BookingController;
import com.eneik.generated.messaging.domain.BookingStatus;
import com.eneik.generated.messaging.domain.NotificationStatus;
import com.eneik.generated.messaging.gateway.InMemoryNotificationGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessagingIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private InMemoryNotificationGateway notificationGateway;

    @Test
    void testBookingCreationAndReminderTriggerFlow() {
        String baseUrl = "http://localhost:" + port;

        BookingController.CreateBookingRequest createRequest = new BookingController.CreateBookingRequest(
                "c-001",
                "+995599000111",
                "test@example.com",
                "m-001",
                "Manicure",
                Instant.now().plusSeconds(3600)
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/api/bookings", createRequest, Map.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        String bookingId = (String) response.getBody().get("id");
        assertNotNull(bookingId);

        // Verify notification list endpoint
        ResponseEntity<List> notifResponse = restTemplate.getForEntity(
                baseUrl + "/api/notifications?bookingId=" + bookingId, List.class
        );
        assertEquals(HttpStatus.OK, notifResponse.getStatusCode());
        assertNotNull(notifResponse.getBody());
        assertFalse(notifResponse.getBody().isEmpty());

        // Trigger reminder scheduler endpoint
        ResponseEntity<List> reminderResponse = restTemplate.postForEntity(
                baseUrl + "/api/reminders/trigger", null, List.class
        );
        assertEquals(HttpStatus.OK, reminderResponse.getStatusCode());
        assertNotNull(reminderResponse.getBody());
    }
}
