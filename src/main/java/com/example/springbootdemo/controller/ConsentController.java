package com.example.springbootdemo.controller;

import com.example.springbootdemo.dto.ConsentRequest;
import com.example.springbootdemo.dto.ConsentResponse;
import com.example.springbootdemo.service.ConsentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consents")
public class ConsentController {

    private final ConsentService consentService;

    public ConsentController(ConsentService consentService) {
        this.consentService = consentService;
    }

    @PostMapping
    public ResponseEntity<ConsentResponse> createConsent(
            @Valid @RequestBody ConsentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(consentService.createConsent(request));
    }

    @GetMapping
    public ResponseEntity<List<ConsentResponse>> getAllConsents(
            @RequestParam(required = false) Long customerId) {

        if (customerId != null) {
            return ResponseEntity.ok(consentService.getConsentsByCustomerId(customerId));
        }
        return ResponseEntity.ok(consentService.getAllConsents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsentResponse> getConsentById(@PathVariable Long id) {
        return ResponseEntity.ok(consentService.getConsentById(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ConsentResponse> approveConsent(@PathVariable Long id) {
        return ResponseEntity.ok(consentService.approveConsent(id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ConsentResponse> rejectConsent(@PathVariable Long id) {
        return ResponseEntity.ok(consentService.rejectConsent(id));
    }
}
