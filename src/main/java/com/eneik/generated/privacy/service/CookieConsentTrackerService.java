package com.eneik.generated.privacy.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CookieConsentTrackerService {

    public List<String> getActiveTrackers(boolean consentGranted) {
        if (!consentGranted) {
            return Collections.emptyList();
        }
        return List.of("facebook-pixel", "google-analytics");
    }

    public boolean isTrackerAllowed(String trackerName, boolean consentGranted) {
        if (!consentGranted) {
            return false;
        }
        return "facebook-pixel".equalsIgnoreCase(trackerName) || "google-analytics".equalsIgnoreCase(trackerName);
    }
}
