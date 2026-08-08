package com.example.springbootdemo.controller;

import com.example.springbootdemo.dto.BeneficiaryRequest;
import com.example.springbootdemo.dto.BeneficiaryResponse;
import com.example.springbootdemo.service.BeneficiaryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(
            BeneficiaryService beneficiaryService) {

        this.beneficiaryService = beneficiaryService;
    }

    @PostMapping
    public ResponseEntity<BeneficiaryResponse> createBeneficiary(
            @Valid @RequestBody BeneficiaryRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        beneficiaryService
                                .createBeneficiary(request)
                );
    }

    @GetMapping
    public ResponseEntity<List<BeneficiaryResponse>>
    getAllBeneficiaries() {

        return ResponseEntity.ok(
                beneficiaryService.getAllBeneficiaries()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeneficiary(
            @PathVariable Long id) {

        beneficiaryService.deleteBeneficiary(id);

        return ResponseEntity.noContent().build();
    }
}