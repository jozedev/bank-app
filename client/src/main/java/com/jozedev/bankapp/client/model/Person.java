package com.jozedev.bankapp.client.model;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@MappedSuperclass
public class Person {

    private String identityNumber;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String address;
    private String phone;

}
