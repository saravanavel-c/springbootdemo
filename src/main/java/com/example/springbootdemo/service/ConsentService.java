package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.ConsentRequest;
import com.example.springbootdemo.dto.ConsentResponse;
import com.example.springbootdemo.entity.Consent;
import com.example.springbootdemo.entity.Customer;
import com.example.springbootdemo.exception.CustomerNotFoundException;
import com.example.springbootdemo.exception.ResourceNotFoundException;
import com.example.springbootdemo.repository.ConsentRepository;
import com.example.springbootdemo.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsentService {

    private final ConsentRepository consentRepository;
    private final CustomerRepository customerRepository;
    private final AuditService auditService;

    public ConsentService(ConsentRepository consentRepository,
                          CustomerRepository customerRepository,
                          AuditService auditService) {
        this.consentRepository = consentRepository;
        this.customerRepository = customerRepository;
        this.auditService = auditService;
    }

    public ConsentResponse createConsent(ConsentRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));

        Consent consent = new Consent();
        consent.setCustomer(customer);
        consent.setPurpose(request.getPurpose());
        consent.setStatus("PENDING");

        Consent savedConsent = consentRepository.save(consent);

        auditService.logAction("CREATE_CONSENT", "CONSENT", savedConsent.getId().toString(),
                "Requested consent: " + savedConsent.getPurpose() + " Ref: " + savedConsent.getConsentReference());

        return convertToResponse(savedConsent);
    }

    public List<ConsentResponse> getAllConsents() {
        return consentRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public ConsentResponse getConsentById(Long id) {
        Consent consent = consentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consent not found with id " + id));
        return convertToResponse(consent);
    }

    public List<ConsentResponse> getConsentsByCustomerId(Long customerId) {
        return consentRepository.findByCustomerId(customerId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public ConsentResponse approveConsent(Long id) {
        Consent consent = consentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consent not found with id " + id));

        consent.setStatus("APPROVED");
        consent.setApprovedAt(LocalDateTime.now());

        Consent updatedConsent = consentRepository.save(consent);

        auditService.logAction("APPROVE_CONSENT", "CONSENT", id.toString(),
                "Approved consent Ref: " + updatedConsent.getConsentReference());

        return convertToResponse(updatedConsent);
    }

    public ConsentResponse rejectConsent(Long id) {
        Consent consent = consentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consent not found with id " + id));

        consent.setStatus("REJECTED");

        Consent updatedConsent = consentRepository.save(consent);

        auditService.logAction("REJECT_CONSENT", "CONSENT", id.toString(),
                "Rejected consent Ref: " + updatedConsent.getConsentReference());

        return convertToResponse(updatedConsent);
    }

    public ConsentResponse convertToResponse(Consent consent) {
        return new ConsentResponse(
                consent.getId(),
                consent.getConsentReference(),
                consent.getCustomer().getId(),
                consent.getCustomer().getName(),
                consent.getStatus(),
                consent.getPurpose(),
                consent.getRequestedAt(),
                consent.getApprovedAt(),
                consent.getExpiresAt()
        );
    }
}
