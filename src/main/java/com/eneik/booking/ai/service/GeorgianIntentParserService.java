package com.eneik.booking.ai.service;

import com.eneik.booking.ai.model.BookingIntentRequest;
import com.eneik.booking.ai.model.BookingIntentResponse;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GeorgianIntentParserService {

    private static final double CONFIDENCE_THRESHOLD = 0.65;

    private final Clock clock;

    public GeorgianIntentParserService() {
        this(Clock.systemUTC());
    }

    public GeorgianIntentParserService(Clock clock) {
        this.clock = clock;
    }

    public BookingIntentResponse parseIntent(BookingIntentRequest request) {
        if (request == null) {
            return buildFallbackResponse("", "Request payload is null");
        }

        String text = request.getTextInput();
        if ((text == null || text.isBlank()) && request.getAudioDataBase64() != null && !request.getAudioDataBase64().isBlank()) {
            text = simulateAudioTranscription(request.getAudioDataBase64());
        }

        if (text == null || text.isBlank()) {
            return buildFallbackResponse("", "No audio or text input provided");
        }

        String normalizedText = text.trim().toLowerCase(Locale.ROOT);

        String extractedService = extractService(normalizedText);
        LocalDateTime requestedTime = extractTime(normalizedText);
        String location = extractLocation(normalizedText);

        double confidenceScore = calculateConfidence(normalizedText, extractedService, requestedTime);
        boolean lowConfidence = confidenceScore < CONFIDENCE_THRESHOLD;

        String status = lowConfidence ? "LOW_CONFIDENCE" : "SUCCESS";

        return new BookingIntentResponse(
                text,
                extractedService,
                requestedTime,
                location,
                confidenceScore,
                lowConfidence,
                status
        );
    }

    private String simulateAudioTranscription(String audioDataBase64) {
        if (audioDataBase64.contains("ambiguous") || audioDataBase64.contains("noise")) {
            return "მინდა რაღაც გაურკვეველი"; // Ambiguous Georgian speech
        }
        return "მინდა თმის შეჭრა ხვალ 15:00 საათზე თბილისში";
    }

    private String extractService(String text) {
        if (text.contains("თმის შეჭრა") || text.contains("ვარცხნილობა") || text.contains("haircut")) {
            return "HAIRCUT";
        } else if (text.contains("მანიკური") || text.contains("manicure")) {
            return "MANICURE";
        } else if (text.contains("მასაჟი") || text.contains("massage")) {
            return "MASSAGE";
        } else if (text.contains("რემონტი") || text.contains("сантехник") || text.contains("plumber")) {
            return "PLUMBING";
        }
        return null;
    }

    private LocalDateTime extractTime(String text) {
        LocalDateTime baseTime = LocalDateTime.now(clock);
        LocalDateTime targetDate = baseTime;

        if (text.contains("ხვალ") || text.contains("tomorrow")) {
            targetDate = baseTime.plusDays(1);
        } else if (text.contains("ზეგ")) {
            targetDate = baseTime.plusDays(2);
        }

        Pattern timePattern = Pattern.compile("(\\d{1,2}):(\\d{2})");
        Matcher matcher = timePattern.matcher(text);
        if (matcher.find()) {
            int hour = Integer.parseInt(matcher.group(1));
            int minute = Integer.parseInt(matcher.group(2));
            if (hour >= 0 && hour < 24 && minute >= 0 && minute < 60) {
                return targetDate.with(LocalTime.of(hour, minute));
            }
        }

        Pattern hourPattern = Pattern.compile("(\\d{1,2}) საათზე");
        Matcher hourMatcher = hourPattern.matcher(text);
        if (hourMatcher.find()) {
            int hour = Integer.parseInt(hourMatcher.group(1));
            if (hour >= 0 && hour < 24) {
                return targetDate.with(LocalTime.of(hour, 0));
            }
        }

        return null;
    }

    private String extractLocation(String text) {
        if (text.contains("თბილის") || text.contains("tbilisi")) {
            return "Tbilisi";
        } else if (text.contains("ბათუმ") || text.contains("batumi")) {
            return "Batumi";
        } else if (text.contains("ქუთაის") || text.contains("kutaisi")) {
            return "Kutaisi";
        }
        return null;
    }

    private double calculateConfidence(String text, String service, LocalDateTime time) {
        if (service == null && time == null) {
            return 0.20;
        }
        if (service == null || time == null) {
            return 0.50;
        }
        if (text.contains("ალბათ") || text.contains("შეიძლება") || text.contains("maybe")) {
            return 0.55;
        }
        return 0.95;
    }

    private BookingIntentResponse buildFallbackResponse(String text, String reason) {
        return new BookingIntentResponse(
                text,
                null,
                null,
                null,
                0.0,
                true,
                "FALLBACK: " + reason
        );
    }
}
