package com.example.springbootdemo.repository;

import com.example.springbootdemo.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeneficiaryRepository
        extends JpaRepository<Beneficiary, Long> {
    List<Beneficiary> findByCustomerId(Long customerId);
}