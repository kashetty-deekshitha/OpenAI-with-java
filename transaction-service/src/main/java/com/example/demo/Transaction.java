package com.example.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Transaction {

    @Id
    @GeneratedValue
    private Long id;

    private String userId;
    private Double amount;
    private String type; // CREDIT / DEBIT
    private LocalDateTime timestamp;

    // getters & setters
}
