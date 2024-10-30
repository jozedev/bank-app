package com.jozedev.bankapp.account.service;

import com.jozedev.bankapp.account.model.dto.ReportDto;
import com.jozedev.bankapp.account.model.dto.TransactionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface TransactionService {

    Flux<TransactionDto> findAll();

    Mono<TransactionDto> findTransactionById(Long id);

    Flux<ReportDto> getReportOfClient(Long clientId, LocalDate startDate, LocalDate endDate);

    Mono<TransactionDto> saveTransaction(TransactionDto newTransaction);

    Mono<TransactionDto> findLatestTransaction(Long accountNumber);

    Mono<Boolean> deleteTransaction(Long id);
}
