package com.example.springbootdemo.repository;

import com.example.springbootdemo.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountId(Long accountId);
    List<Transaction> findByAccountIdOrderByTransactionDateDesc(Long accountId);
    List<Transaction> findAllByOrderByTransactionDateDesc();
    Optional<Transaction> findByTransactionReference(String transactionReference);
}