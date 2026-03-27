package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService service;

    @GetMapping("/last5")
    public List<Transaction> getLast5(@RequestParam String userId) {
        return service.getLast5(userId);
    }

    @GetMapping("/highest")
    public Transaction getHighest(@RequestParam String userId) {
        return service.getHighest(userId);
    }
}