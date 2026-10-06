package com.eneik.generated.privacy;

import com.eneik.generated.privacy.dto.*;
import com.eneik.generated.privacy.model.UserDataRecord;
import com.eneik.generated.privacy.service.PrivacyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PrivacyServiceTest {

    private final Instant fixedInstant = Instant.parse("2026-10-06T12:00:00Z");
    private final Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));
    private PrivacyService privacyService;

    @BeforeEach
    void setUp() {
        privacyService = new PrivacyService(fixedClock);
    }

    @Test
    void testDataExportGeneratesCompleteJsonDossier() {
        String userId = "user-12345";
        UserDataRecord userRecord = new UserDataRecord(
                userId,
                "+995555112233",
                "fb-id-123",
                "Giga Giorgadze",
                "TBILISI",
                fixedInstant.minusSeconds(86400)
        );
        userRecord.setMasterId("master-99");
        userRecord.setFacebookPageId("fb-page-99");
        userRecord.setPublicProfileUrl("https://georgianmasters.ge/m/giga");
        userRecord.setDescription("Top Barber Tbilisi");
        userRecord.setAddress("Chavchavadze Ave 12, Tbilisi");
        userRecord.getImportedPhotos().add("https://fb.com/p1.jpg");

        BookingExportItem booking = new BookingExportItem(
                "b1111111-1111-1111-1111-111111111111",
                "master-99",
                "Haircut",
                35.00,
                fixedInstant,
                fixedInstant.plusSeconds(3600),
                "CONFIRMED",
                "TBC_TRANSFER"
        );
        userRecord.getBookings().add(booking);

        VoiceBookingExportItem voiceReq = new VoiceBookingExportItem(
                "v2222222-2222-2222-2222-222222222222",
                "თმის შეჭრა მინდა ხვალ 3 საათზე",
                5.5f,
                fixedInstant
        );
        userRecord.getVoiceRequests().add(voiceReq);

        NotificationLogExportItem notification = new NotificationLogExportItem(
                "MESSENGER",
                "BOOKING_NOTIFICATION",
                fixedInstant
        );
        userRecord.getMessengerNotifications().add(notification);

        privacyService.registerUserData(userRecord);

        PrivacyExportRequest request = new PrivacyExportRequest("JSON", true, null);
        PrivacyExportStatusResponse response = privacyService.requestExport(userId, request);

        assertNotNull(response);
        assertEquals("COMPLETED", response.status());
        assertEquals(userId, response.userId());
        assertNotNull(response.exportId());
        assertEquals(fixedInstant, response.requestedAt());

        PrivacyExportPayload payload = response.payload();
        assertNotNull(payload);
        assertEquals("1.0", payload.schemaVersion());
        assertEquals(fixedInstant, payload.exportedAt());

        assertNotNull(payload.userProfile());
        assertEquals("+995555112233", payload.userProfile().phone());
        assertEquals("fb-id-123", payload.userProfile().facebookId());
        assertEquals("Giga Giorgadze", payload.userProfile().fullName());
        assertEquals("TBILISI", payload.userProfile().city());

        assertNotNull(payload.masterProfile());
        assertEquals("master-99", payload.masterProfile().masterId());
        assertEquals("fb-page-99", payload.masterProfile().facebookPageId());
        assertEquals(1, payload.masterProfile().importedPhotos().size());

        assertEquals(1, payload.bookings().size());
        assertEquals("Haircut", payload.bookings().get(0).serviceName());
        assertEquals(35.0, payload.bookings().get(0).priceGel(), 0.001);

        assertEquals(1, payload.voiceRequests().size());
        assertEquals("თმის შეჭრა მინდა ხვალ 3 საათზე", payload.voiceRequests().get(0).transcribedText());

        assertEquals(1, payload.messengerNotifications().size());
        assertEquals("MESSENGER", payload.messengerNotifications().get(0).channel());

        // Test export retrieval
        Optional<PrivacyExportStatusResponse> retrieved = privacyService.getExportStatus(response.exportId());
        assertTrue(retrieved.isPresent());
        assertEquals(response.exportId(), retrieved.get().exportId());
    }

    @Test
    void testErasureRequestHardDeletesAndAnonymizesData() {
        String userId = "user-67890";
        UserDataRecord userRecord = new UserDataRecord(
                userId,
                "+995599887766",
                "fb-id-67890",
                "Nino Beridze",
                "BATUMI",
                fixedInstant.minusSeconds(172800)
        );
        userRecord.setFacebookPageId("fb-page-batumi");
        userRecord.getImportedPhotos().add("https://fb.com/batumi.jpg");
        userRecord.getBookings().add(new BookingExportItem(
                "b3333333-3333-3333-3333-333333333333",
                "m1", "Manicure", 25.0, fixedInstant, fixedInstant.plusSeconds(1800), "CONFIRMED", "CASH"
        ));

        privacyService.registerUserData(userRecord);

        PrivacyErasureRequest erasureRequest = new PrivacyErasureRequest(true, "ALL_DATA", "User requested account deletion");
        PrivacyErasureResponse erasureResponse = privacyService.requestErasure(userId, erasureRequest);

        assertNotNull(erasureResponse);
        assertEquals("COMPLETED", erasureResponse.status());
        assertEquals(userId, erasureResponse.userId());
        assertNotNull(erasureResponse.erasureRequestId());
        assertNotNull(erasureResponse.confirmationCode());

        // Verify data was hard deleted / anonymized in record
        Optional<UserDataRecord> updatedRecord = privacyService.getUserData(userId);
        assertTrue(updatedRecord.isPresent());
        UserDataRecord record = updatedRecord.get();
        assertTrue(record.isErased());
        assertEquals("[DELETED]", record.getPhone());
        assertNull(record.getFacebookId());
        assertEquals("ANONYMIZED_USER", record.getFullName());
        assertNull(record.getFacebookPageId());
        assertTrue(record.getImportedPhotos().isEmpty());
        assertTrue(record.getBookings().isEmpty());

        // Subsequent export should return anonymized payload without personal identifiers
        PrivacyExportStatusResponse postErasureExport = privacyService.requestExport(userId, new PrivacyExportRequest("JSON", true, null));
        assertEquals("+995000000000", postErasureExport.payload().userProfile().phone());
        assertNull(postErasureExport.payload().userProfile().facebookId());
        assertEquals("Anonymized User", postErasureExport.payload().userProfile().fullName());
        assertTrue(postErasureExport.payload().bookings().isEmpty());
    }

    @Test
    void testErasureRequestRequiresConfirmation() {
        PrivacyErasureRequest unconfirmedRequest = new PrivacyErasureRequest(false, "ALL_DATA", "No confirmation");
        assertThrows(IllegalArgumentException.class, () -> privacyService.requestErasure("u-1", unconfirmedRequest));
    }
}
