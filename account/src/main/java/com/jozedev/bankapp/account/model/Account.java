package com.jozedev.bankapp.account.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Account {

    @jakarta.persistence.Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountNumber;
    private String type;
    private double initialBalance;
    private boolean active;
    @Column(nullable = false)
    private Long clientId;
}
