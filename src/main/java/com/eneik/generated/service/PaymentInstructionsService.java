package com.eneik.generated.service;

import com.eneik.generated.domain.PaymentInstructions;
import com.eneik.generated.dto.PaymentInstructionsRequest;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentInstructionsService {

    private final Map<String, PaymentInstructions> storage = new ConcurrentHashMap<>();
    private final Clock clock;

    public PaymentInstructionsService(Clock clock) {
        this.clock = clock;
    }

    public Optional<PaymentInstructions> getPaymentInstructions(String professionalId) {
        if (professionalId == null || professionalId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(professionalId));
    }

    public PaymentInstructions saveOrUpdatePaymentInstructions(String professionalId, PaymentInstructionsRequest request) {
        if (professionalId == null || professionalId.isBlank()) {
            throw new IllegalArgumentException("Professional ID must not be blank");
        }
        if (request == null || request.getAccountHolderName() == null || request.getAccountHolderName().isBlank()) {
            throw new IllegalArgumentException("Account holder name must not be blank");
        }

        Instant now = Instant.now(clock);
        PaymentInstructions instructions = new PaymentInstructions(
                professionalId,
                request.getAccountHolderName(),
                request.getTbcIban(),
                request.getBogIban(),
                Boolean.TRUE.equals(request.getAcceptsCash()),
                request.getInstructionsNote(),
                now
        );

        storage.put(professionalId, instructions);
        return instructions;
    }

    public void clear() {
        storage.clear();
    }
}
