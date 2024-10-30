package com.jozedev.bankapp.account.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ClientDto {
    private Long clientId;
    private String identityNumber;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String address;
    private String password;
    private String phone;
    private boolean active;
}
