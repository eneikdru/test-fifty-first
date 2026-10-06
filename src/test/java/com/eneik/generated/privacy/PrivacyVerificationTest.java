package com.eneik.generated.privacy;

import com.eneik.generated.privacy.controller.PrivacyController;
import com.eneik.generated.privacy.dto.*;
import com.eneik.generated.privacy.model.UserDataRecord;
import com.eneik.generated.privacy.service.CookieConsentTrackerService;
import com.eneik.generated.privacy.service.PrivacyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PrivacyVerificationTest {

    private final Instant fixedInstant = Instant.parse("2026-10-06T12:00:00Z");
    private final Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));

    private PrivacyService privacyService;
    private PrivacyController privacyController;
    private CookieConsentTrackerService cookieConsentTrackerService;

    @BeforeEach
    void setUp() {
        privacyService = new PrivacyService(fixedClock);
        privacyController = new PrivacyController(privacyService, fixedClock);
        cookieConsentTrackerService = new CookieConsentTrackerService();
    }

    @Test
    @DisplayName("Given an erased user, When queried, Then the system returns no personal data")
    void givenErasedUser_whenQueried_thenReturnsNoPersonalData() {
        // Arrange: create user with sensitive personal data and bookings
        String userId = "user-erased-verification-1";
        UserDataRecord record = new UserDataRecord(
                userId,
                "+995599112233",
                "fb-user-999",
                "David Maisuradze",
                "BATUMI",
                fixedInstant.minusSeconds(86400)
        );
        record.setMasterId("master-batumi-1");
        record.setFacebookPageId("fb-page-batumi-1");
        record.setPublicProfileUrl("https://georgianmasters.ge/m/david");
        record.setDescription("Experienced stylist in Batumi");
        record.setAddress("Gamsakhurdia St 5, Batumi");
        record.getImportedPhotos().add("https://fb.com/photo1.jpg");

        record.getBookings().add(new BookingExportItem(
                "b-100", "master-batumi-1", "Haircut & Styling", 40.0,
                fixedInstant, fixedInstant.plusSeconds(3600), "CONFIRMED", "CASH"
        ));
        record.getVoiceRequests().add(new VoiceBookingExportItem(
                "v-100", "ჩაწერა მინდა ხვალ 5 საათზე", 4.2f, fixedInstant
        ));
        record.getMessengerNotifications().add(new NotificationLogExportItem(
                "MESSENGER", "BOOKING_CONFIRMED", fixedInstant
        ));

        privacyService.registerUserData(record);

        // Act: Execute erasure request
        PrivacyErasureRequest erasureRequest = new PrivacyErasureRequest(true, "ALL_DATA", "Privacy regulation compliance request");
        PrivacyErasureResponse erasureResponse = privacyService.requestErasure(userId, erasureRequest);

        // Assert: Erasure response is COMPLETED
        assertNotNull(erasureResponse);
        assertEquals("COMPLETED", erasureResponse.status());
        assertEquals(userId, erasureResponse.userId());

        // Assert: Direct record query contains NO personal data
        Optional<UserDataRecord> queriedRecordOpt = privacyService.getUserData(userId);
        assertTrue(queriedRecordOpt.isPresent(), "User record should remain in system as anonymized entry");
        UserDataRecord erasedRecord = queriedRecordOpt.get();

        assertTrue(erasedRecord.isErased(), "Record must be marked as erased");
        assertEquals("[DELETED]", erasedRecord.getPhone(), "Phone number must be wiped/anonymized");
        assertNull(erasedRecord.getFacebookId(), "Facebook ID must be null");
        assertEquals("ANONYMIZED_USER", erasedRecord.getFullName(), "Full name must be anonymized");
        assertNull(erasedRecord.getFacebookPageId(), "Facebook Page ID must be cleared");
        assertNull(erasedRecord.getDescription(), "Profile description must be cleared");
        assertNull(erasedRecord.getAddress(), "Address must be cleared");
        assertTrue(erasedRecord.getImportedPhotos().isEmpty(), "Imported photos must be empty");
        assertTrue(erasedRecord.getBookings().isEmpty(), "Bookings list must be empty");
        assertTrue(erasedRecord.getVoiceRequests().isEmpty(), "Voice requests list must be empty");
        assertTrue(erasedRecord.getMessengerNotifications().isEmpty(), "Messenger notifications list must be empty");

        // Assert: Privacy export payload for erased user contains NO personal data
        PrivacyExportStatusResponse exportResponse = privacyService.requestExport(userId, new PrivacyExportRequest("JSON", true, null));
        assertNotNull(exportResponse);
        assertEquals("COMPLETED", exportResponse.status());

        PrivacyExportPayload payload = exportResponse.payload();
        assertNotNull(payload);
        assertNotNull(payload.userProfile());
        assertEquals("+995000000000", payload.userProfile().phone(), "Export phone must be anonymized placeholder");
        assertNull(payload.userProfile().facebookId(), "Export facebookId must be null");
        assertEquals("Anonymized User", payload.userProfile().fullName(), "Export fullName must be anonymized");
        assertNull(payload.masterProfile(), "Export masterProfile must be null for erased user");
        assertTrue(payload.bookings().isEmpty(), "Export bookings must be empty");
        assertTrue(payload.voiceRequests().isEmpty(), "Export voice requests must be empty");
        assertTrue(payload.messengerNotifications().isEmpty(), "Export notifications must be empty");
    }

    @Test
    @DisplayName("Given a refused cookie consent, When browsing, Then no analytics trackers are loaded")
    void givenRefusedCookieConsent_whenBrowsing_thenNoAnalyticsTrackersLoaded() {
        // Arrange & Act: Query CookieConsentTrackerService with refused consent (consentGranted = false)
        boolean consentGranted = false;
        List<String> activeTrackers = cookieConsentTrackerService.getActiveTrackers(consentGranted);

        // Assert: Active trackers list is empty and individual trackers are prohibited
        assertTrue(activeTrackers.isEmpty(), "No analytics trackers should be loaded when cookie consent is refused");
        assertFalse(cookieConsentTrackerService.isTrackerAllowed("facebook-pixel", consentGranted), "Facebook Pixel must not be allowed when consent is refused");
        assertFalse(cookieConsentTrackerService.isTrackerAllowed("google-analytics", consentGranted), "Google Analytics must not be allowed when consent is refused");

        // Verification of allowed trackers when consent IS granted
        List<String> trackersWhenConsented = cookieConsentTrackerService.getActiveTrackers(true);
        assertEquals(2, trackersWhenConsented.size());
        assertTrue(trackersWhenConsented.contains("facebook-pixel"));
        assertTrue(trackersWhenConsented.contains("google-analytics"));
    }
}
