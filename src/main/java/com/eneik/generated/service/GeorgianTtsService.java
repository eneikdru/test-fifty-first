package com.eneik.generated.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class GeorgianTtsService {

    public static class AudioSynthesisResult {
        private final String textResponse;
        private final String audioBase64;
        private final String audioFormat;

        public AudioSynthesisResult(String textResponse, String audioBase64, String audioFormat) {
            this.textResponse = textResponse;
            this.audioBase64 = audioBase64;
            this.audioFormat = audioFormat;
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

    public AudioSynthesisResult synthesizeSlotProposal(String masterName, String serviceName, String slotTime) {
        String georgianText = String.format(
            "შემოთავაზებული დრო: %s. ოსტატი: %s. მომსახურება: %s. გსურთ დადასტურება?",
            slotTime != null ? slotTime : "თავისუფალი სლოტი",
            masterName != null ? masterName : "მაოსტატი",
            serviceName != null ? serviceName : "სერვისი"
        );

        // Synthesize deterministic pseudo-audio byte stream (WAV header container + text payload)
        byte[] fakeWavHeader = new byte[] {
            'R', 'I', 'F', 'F', 36, 0, 0, 0, 'W', 'A', 'V', 'E',
            'f', 'm', 't', ' ', 16, 0, 0, 0, 1, 0, 1, 0, 64, 31, 0, 0
        };
        byte[] textBytes = georgianText.getBytes(StandardCharsets.UTF_8);
        byte[] combinedAudio = new byte[fakeWavHeader.length + textBytes.length];
        System.arraycopy(fakeWavHeader, 0, combinedAudio, 0, fakeWavHeader.length);
        System.arraycopy(textBytes, 0, combinedAudio, fakeWavHeader.length, textBytes.length);

        String encodedAudio = Base64.getEncoder().encodeToString(combinedAudio);

        return new AudioSynthesisResult(georgianText, encodedAudio, "audio/wav");
    }
}
