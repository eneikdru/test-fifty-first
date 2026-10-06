package com.eneik.generated.controller;

import com.eneik.generated.domain.PaymentInstructions;
import com.eneik.generated.dto.PaymentInstructionsRequest;
import com.eneik.generated.service.PaymentInstructionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/professionals/{professionalId}/payment-instructions")
public class PaymentInstructionsController {

    private final PaymentInstructionsService service;

    public PaymentInstructionsController(PaymentInstructionsService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PaymentInstructions> getPaymentInstructions(@PathVariable String professionalId) {
        return service.getPaymentInstructions(professionalId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<?> updatePaymentInstructions(@PathVariable String professionalId,
                                                        @RequestBody PaymentInstructionsRequest request) {
        try {
            PaymentInstructions instructions = service.saveOrUpdatePaymentInstructions(professionalId, request);
            return ResponseEntity.ok(instructions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
