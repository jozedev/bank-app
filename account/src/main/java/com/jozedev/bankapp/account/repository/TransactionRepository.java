package com.jozedev.bankapp.account.repository;

import com.jozedev.bankapp.account.model.Transaction;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface TransactionRepository extends R2dbcRepository<Transaction, Long> {

    @Query(value = "SElECT * FROM transaction t WHERE t.account_number = :accountNumber ORDER BY t.id DESC LIMIT 1")
    Mono<Transaction> findLastTransaction(Long accountNumber);

    @Query(value = """
            SELECT * FROM transaction
            WHERE account_number = :accountNumber
            AND date BETWEEN :startDate AND :endDate
            ORDER BY id DESC
        """)
    Flux<Transaction> getAccountReport(Long accountNumber, LocalDate startDate, LocalDate endDate);
}
