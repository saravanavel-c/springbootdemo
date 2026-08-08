package com.example.springbootdemo.repository;

import com.example.springbootdemo.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneficiaryRepository
        extends JpaRepository<Beneficiary, Long> {
}