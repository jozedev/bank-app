package com.jozedev.bankapp.client.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String address;
    private String password;
    private String phone;
    private boolean active;
}
