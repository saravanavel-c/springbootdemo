package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.BeneficiaryRequest;
import com.example.springbootdemo.dto.BeneficiaryResponse;
import com.example.springbootdemo.entity.Beneficiary;
import com.example.springbootdemo.exception.ResourceNotFoundException;
import com.example.springbootdemo.repository.BeneficiaryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;

    public BeneficiaryService(
            BeneficiaryRepository beneficiaryRepository) {

        this.beneficiaryRepository = beneficiaryRepository;
    }

    public BeneficiaryResponse createBeneficiary(
            BeneficiaryRequest request) {

        Beneficiary beneficiary = new Beneficiary();

        beneficiary.setName(request.getName());
        beneficiary.setAccountNumber(request.getAccountNumber());
        beneficiary.setBankName(request.getBankName());

        Beneficiary saved =
                beneficiaryRepository.save(beneficiary);

        return convertToResponse(saved);
    }

    public List<BeneficiaryResponse> getAllBeneficiaries() {

        return beneficiaryRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public void deleteBeneficiary(Long id) {

        if (!beneficiaryRepository.existsById(id)) {

            throw new ResourceNotFoundException(
                    "Beneficiary not found with id " + id
            );
        }

        beneficiaryRepository.deleteById(id);
    }

    private BeneficiaryResponse convertToResponse(
            Beneficiary beneficiary) {

        return new BeneficiaryResponse(
                beneficiary.getId(),
                beneficiary.getName(),
                beneficiary.getAccountNumber(),
                beneficiary.getBankName()
        );
    }
}