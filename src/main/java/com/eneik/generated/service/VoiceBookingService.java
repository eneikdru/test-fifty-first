package com.eneik.generated.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Service
public class VoiceBookingService {

    public enum BookingStatus {
        PROPOSED,
        COMPLETED
    }

    public static class VoiceBooking {
        private final String id;
        private final String masterId;
        private final String serviceName;
        private final String slotTime;
        private final BookingStatus status;
        private final String textResponse;
        private final String audioBase64;
        private final String audioFormat;

        public VoiceBooking(
            String id,
            String masterId,
            String serviceName,
            String slotTime,
            BookingStatus status,
            String textResponse,
            String audioBase64,
            String audioFormat
        ) {
            this.id = id;
            this.masterId = masterId;
            this.serviceName = serviceName;
            this.slotTime = slotTime;
            this.status = status;
            this.textResponse = textResponse;
            this.audioBase64 = audioBase64;
            this.audioFormat = audioFormat;
        }

        public String getId() {
            return id;
        }

        public String getMasterId() {
            return masterId;
        }

        public String getServiceName() {
            return serviceName;
        }

        public String getSlotTime() {
            return slotTime;
        }

        public BookingStatus getStatus() {
            return status;
        }

        public String getTextResponse() {
            return textResponse;
        }

        public String getAudioBase64() {
            return audioBase64;
        }

        public String getAudioFormat() {
            return audioFormat;
        }
    }

    private final GeorgianTtsService ttsService;
    private final Map<String, VoiceBooking> bookings = new ConcurrentHashMap<>();
    private Supplier<String> idGenerator = () -> UUID.randomUUID().toString();

    public VoiceBookingService(GeorgianTtsService ttsService) {
        this.ttsService = ttsService;
    }

    public void setIdGenerator(Supplier<String> idGenerator) {
        this.idGenerator = idGenerator;
    }

    public VoiceBooking proposeSlot(String masterId, String serviceName, String slotTime) {
        String bookingId = idGenerator.get();
        GeorgianTtsService.AudioSynthesisResult ttsResult = ttsService.synthesizeSlotProposal(masterId, serviceName, slotTime);

        VoiceBooking booking = new VoiceBooking(
            bookingId,
            masterId,
            serviceName,
            slotTime,
            BookingStatus.PROPOSED,
            ttsResult.getTextResponse(),
            ttsResult.getAudioBase64(),
            ttsResult.getAudioFormat()
        );

        bookings.put(bookingId, booking);
        return booking;
    }

    public Optional<VoiceBooking> getBooking(String bookingId) {
        return Optional.ofNullable(bookings.get(bookingId));
    }

    /**
     * Atomically transitions booking status from PROPOSED to COMPLETED.
     */
    public Optional<VoiceBooking> confirmBooking(String bookingId) {
        if (bookingId == null || !bookings.containsKey(bookingId)) {
            return Optional.empty();
        }

        VoiceBooking updated = bookings.computeIfPresent(bookingId, (id, current) -> {
            if (current.getStatus() == BookingStatus.COMPLETED) {
                return current;
            }
            return new VoiceBooking(
                current.getId(),
                current.getMasterId(),
                current.getServiceName(),
                current.getSlotTime(),
                BookingStatus.COMPLETED,
                current.getTextResponse(),
                current.getAudioBase64(),
                current.getAudioFormat()
            );
        });

        return Optional.ofNullable(updated);
    }
}
