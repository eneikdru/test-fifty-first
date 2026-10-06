package com.eneik.generated.privacy.service;

import com.eneik.generated.privacy.dto.*;
import com.eneik.generated.privacy.model.UserDataRecord;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PrivacyService {

    private final Clock clock;
    private final Map<String, UserDataRecord> userStore = new ConcurrentHashMap<>();
    private final Map<String, PrivacyExportStatusResponse> exportStore = new ConcurrentHashMap<>();
    private final Map<String, PrivacyErasureResponse> erasureStore = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1000);

    public PrivacyService() {
        this(Clock.systemUTC());
    }

    public PrivacyService(Clock clock) {
        this.clock = clock;
    }

    public void registerUserData(UserDataRecord record) {
        userStore.put(record.getUserId(), record);
    }

    public Optional<UserDataRecord> getUserData(String userId) {
        return Optional.ofNullable(userStore.get(userId));
    }

    public PrivacyExportStatusResponse requestExport(String userId, PrivacyExportRequest request) {
        Instant now = clock.instant();
        String exportId = UUID.nameUUIDFromBytes(("export-" + userId + "-" + idCounter.incrementAndGet()).getBytes()).toString();

        UserDataRecord record = userStore.get(userId);

        UserProfileExport userProfile = null;
        MasterProfileExport masterProfile = null;
        List<BookingExportItem> bookings = Collections.emptyList();
        List<VoiceBookingExportItem> voiceRequests = Collections.emptyList();
        List<NotificationLogExportItem> messengerNotifications = Collections.emptyList();

        if (record != null && !record.isErased()) {
            userProfile = new UserProfileExport(
                    record.getUserId(),
                    record.getPhone(),
                    record.getFacebookId(),
                    record.getFullName(),
                    record.getCity(),
                    record.getCreatedAt()
            );

            if (record.getMasterId() != null) {
                masterProfile = new MasterProfileExport(
                        record.getMasterId(),
                        record.getFacebookPageId(),
                        record.getPublicProfileUrl(),
                        record.getDescription(),
                        record.getAddress(),
                        Boolean.TRUE.equals(request.includeMedia()) ? record.getImportedPhotos() : Collections.emptyList(),
                        Collections.emptyList()
                );
            }

            bookings = new ArrayList<>(record.getBookings());
            voiceRequests = new ArrayList<>(record.getVoiceRequests());
            messengerNotifications = new ArrayList<>(record.getMessengerNotifications());
        } else {
            userProfile = new UserProfileExport(
                    userId,
                    "+995000000000",
                    null,
                    "Anonymized User",
                    "TBILISI",
                    now
            );
        }

        PrivacyExportPayload payload = new PrivacyExportPayload(
                "1.0",
                now,
                userProfile,
                masterProfile,
                bookings,
                voiceRequests,
                messengerNotifications
        );

        PrivacyExportStatusResponse response = new PrivacyExportStatusResponse(
                exportId,
                userId,
                "COMPLETED",
                now,
                now,
                "https://api.georgianmasters.ge/exports/" + exportId + ".json",
                payload
        );

        exportStore.put(exportId, response);
        return response;
    }

    public Optional<PrivacyExportStatusResponse> getExportStatus(String exportId) {
        return Optional.ofNullable(exportStore.get(exportId));
    }

    public PrivacyErasureResponse requestErasure(String userId, PrivacyErasureRequest request) {
        if (!Boolean.TRUE.equals(request.confirmErasure())) {
            throw new IllegalArgumentException("Erasure confirmation required");
        }

        Instant now = clock.instant();
        String erasureRequestId = UUID.nameUUIDFromBytes(("erasure-" + userId + "-" + idCounter.incrementAndGet()).getBytes()).toString();

        UserDataRecord record = userStore.get(userId);
        if (record != null) {
            record.setErased(true);
            record.setPhone("[DELETED]");
            record.setFacebookId(null);
            record.setFullName("ANONYMIZED_USER");
            record.setFacebookPageId(null);
            record.setDescription(null);
            record.setAddress(null);
            record.getImportedPhotos().clear();
            record.getBookings().clear();
            record.getVoiceRequests().clear();
            record.getMessengerNotifications().clear();
        }

        String confirmationCode = "DEL-CONF-" + Math.abs(erasureRequestId.hashCode());

        PrivacyErasureResponse response = new PrivacyErasureResponse(
                erasureRequestId,
                userId,
                "COMPLETED",
                now,
                now,
                now,
                confirmationCode
        );

        erasureStore.put(erasureRequestId, response);
        return response;
    }

    public Optional<PrivacyErasureResponse> getErasureStatus(String erasureRequestId) {
        return Optional.ofNullable(erasureStore.get(erasureRequestId));
    }
}
