package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repo;

    public List<Transaction> getLast5(String userId) {
        return repo.findTop5ByUserIdOrderByTimestampDesc(userId);
    }

    public Transaction getHighest(String userId) {
        return repo.findTopByUserIdOrderByAmountDesc(userId);
    }
}
