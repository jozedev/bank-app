package com.jozedev.bankapp.account.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReportDto {

    private Long accountNumber;
    private String accountType;
    private double initialBalance;
    private double accountBalance;
    private boolean active;
    private List<TransactionDto> transactions;

}
