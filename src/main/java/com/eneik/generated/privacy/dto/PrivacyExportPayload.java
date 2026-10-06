package com.eneik.generated.privacy.dto;

import java.time.Instant;
import java.util.List;

public record PrivacyExportPayload(
        String schemaVersion,
        Instant exportedAt,
        UserProfileExport userProfile,
        MasterProfileExport masterProfile,
        List<BookingExportItem> bookings,
        List<VoiceBookingExportItem> voiceRequests,
        List<NotificationLogExportItem> messengerNotifications
) {}
