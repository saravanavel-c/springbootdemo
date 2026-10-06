package com.example.springbootdemo.controller;

import com.example.springbootdemo.dto.TransferRequest;
import com.example.springbootdemo.dto.TransferResponse;
import com.example.springbootdemo.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> executeTransfer(
            @Valid @RequestBody TransferRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transferService.executeTransfer(request));
    }
}
