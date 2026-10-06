package com.example.springbootdemo.repository;

import com.example.springbootdemo.entity.Consent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {
    List<Consent> findByCustomerId(Long customerId);
    Optional<Consent> findByConsentReference(String consentReference);
}
