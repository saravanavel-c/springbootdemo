package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.BeneficiaryRequest;
import com.example.springbootdemo.dto.BeneficiaryResponse;
import com.example.springbootdemo.entity.Beneficiary;
import com.example.springbootdemo.entity.Customer;
import com.example.springbootdemo.exception.CustomerNotFoundException;
import com.example.springbootdemo.exception.ResourceNotFoundException;
import com.example.springbootdemo.repository.BeneficiaryRepository;
import com.example.springbootdemo.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final CustomerRepository customerRepository;
    private final AuditService auditService;

    public BeneficiaryService(BeneficiaryRepository beneficiaryRepository,
                              CustomerRepository customerRepository,
                              AuditService auditService) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.customerRepository = customerRepository;
        this.auditService = auditService;
    }

    public BeneficiaryResponse createBeneficiary(BeneficiaryRequest request) {
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setName(request.getName());
        beneficiary.setAccountNumber(request.getAccountNumber());
        beneficiary.setBankName(request.getBankName());
        beneficiary.setIfsc(request.getIfsc());
        beneficiary.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");

        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));
            beneficiary.setCustomer(customer);
        }

        Beneficiary savedBeneficiary = beneficiaryRepository.save(beneficiary);

        auditService.logAction("CREATE_BENEFICIARY", "BENEFICIARY", savedBeneficiary.getId().toString(),
                "Added beneficiary " + savedBeneficiary.getName() + " (" + savedBeneficiary.getAccountNumber() + ")");

        return convertToResponse(savedBeneficiary);
    }

    public List<BeneficiaryResponse> getAllBeneficiaries() {
        return beneficiaryRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<BeneficiaryResponse> getBeneficiariesByCustomerId(Long customerId) {
        return beneficiaryRepository.findByCustomerId(customerId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public void deleteBeneficiary(Long id) {
        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with id " + id));

        beneficiaryRepository.delete(beneficiary);

        auditService.logAction("DELETE_BENEFICIARY", "BENEFICIARY", id.toString(),
                "Deleted beneficiary " + beneficiary.getName());
    }

    public BeneficiaryResponse convertToResponse(Beneficiary beneficiary) {
        return new BeneficiaryResponse(
                beneficiary.getId(),
                beneficiary.getName(),
                beneficiary.getAccountNumber(),
                beneficiary.getBankName(),
                beneficiary.getIfsc(),
                beneficiary.getStatus(),
                beneficiary.getCustomer() != null ? beneficiary.getCustomer().getId() : null,
                beneficiary.getCreatedAt()
        );
    }
}