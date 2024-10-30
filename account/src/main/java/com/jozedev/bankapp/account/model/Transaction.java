package com.jozedev.bankapp.account.model;

import java.time.LocalDate;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Transaction {

	@jakarta.persistence.Id
	@org.springframework.data.annotation.Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDate date;
	private String type;
	private double amount;
	private double balance;

	@Column(name = "account_number", nullable = false)
	private Long accountNumber;
}
