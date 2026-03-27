package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findTop5ByUserIdOrderByTimestampDesc(String userId);

    Transaction findTopByUserIdOrderByAmountDesc(String userId);
}