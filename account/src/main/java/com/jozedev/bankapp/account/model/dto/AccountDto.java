package com.jozedev.bankapp.account.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDto {
    private Long accountNumber;
    private String type;
    private double initialBalance;
    private boolean active;

    private Long clientId;
}
