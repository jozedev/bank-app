package com.jozedev.bankapp.account.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TransactionDto {
    private Long id;
    private LocalDate date;
    private String type;
    private double amount;
    private double balance;
    private Long accountNumber;
}
