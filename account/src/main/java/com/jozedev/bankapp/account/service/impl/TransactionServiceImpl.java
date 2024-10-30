package com.jozedev.bankapp.account.service.impl;

import com.jozedev.bankapp.account.exception.NotActiveException;
import com.jozedev.bankapp.account.exception.NotFoundException;
import com.jozedev.bankapp.account.exception.NotEnoughFundsException;
import com.jozedev.bankapp.account.model.Transaction;
import com.jozedev.bankapp.account.model.dto.ReportDto;
import com.jozedev.bankapp.account.model.dto.TransactionDto;
import com.jozedev.bankapp.account.repository.TransactionRepository;
import com.jozedev.bankapp.account.service.AccountService;
import com.jozedev.bankapp.account.service.TransactionService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    @Override
    public Flux<TransactionDto> findAll() {
        return transactionRepository.findAll().map(this::mapToDto);
    }

    @Override
    public Mono<TransactionDto> findTransactionById(Long id) {
        return transactionRepository.findById(id).map(this::mapToDto);
    }

    @Override
    public Mono<TransactionDto> findLatestTransaction(Long accountNumber) {
        return transactionRepository.findLastTransaction(accountNumber).map(this::mapToDto);
    }

    @Override
    public Flux<ReportDto> getReportOfClient(Long clientId, LocalDate startDate, LocalDate endDate) {
        return accountService.findByClientId(clientId)
                .flatMap(account ->
                        transactionRepository.findLastTransaction(account.getAccountNumber())
                                .map(Transaction::getBalance)
                                .defaultIfEmpty(account.getInitialBalance())
                                .flatMapMany(balance ->
                                        transactionRepository.getAccountReport(account.getAccountNumber(), startDate, endDate)
                                                .map(this::mapToDto)
                                                .collectList()
                                                .map(transactions ->
                                                        ReportDto.builder()
                                                                .accountNumber(account.getAccountNumber())
                                                                .accountType(account.getType())
                                                                .initialBalance(account.getInitialBalance())
                                                                .active(account.isActive())
                                                                .accountBalance(balance)
                                                                .transactions(transactions)
                                                                .build()
                                                )
                                )
                )
                .switchIfEmpty(Mono.error(new NotFoundException("No se encontraron cuentas para el cliente: " + clientId)));
    }

    @Override
    public Mono<TransactionDto> saveTransaction(TransactionDto newTransactionDto) {
        // Verify that the account exists
        return accountService
                .findAccountByNumber(newTransactionDto.getAccountNumber())
                .switchIfEmpty(Mono.error(new NotFoundException("La cuenta de número " +
                        newTransactionDto.getAccountNumber() + " no existe")))
                .flatMap(account -> {
                    if (!account.isActive()) {
                        return Mono.error(new NotActiveException("La cuenta con id '%d' no está activa".formatted(account.getClientId())));
                    }

                    return Mono.just(account);
                })
                .flatMap( account -> {
                    newTransactionDto.setBalance(account.getInitialBalance() + newTransactionDto.getAmount());

                    // Check if the account has at least a transaction
                    return transactionRepository
                            .findLastTransaction(newTransactionDto.getAccountNumber())
                            .map(Transaction::getBalance)
                            .defaultIfEmpty(account.getInitialBalance())
                            .flatMap(lastBalance -> {
                                newTransactionDto.setBalance(lastBalance + newTransactionDto.getAmount());
                                // Check if the balance is positive
                                if (newTransactionDto.getBalance() < 0) {
                                    return Mono.error(new NotEnoughFundsException("Saldo no disponible"));
                                }

                                return Mono.just(newTransactionDto);
                            });
                })
                .map(this::mapFromDto)
                .flatMap(transactionRepository::save)
                .map(this::mapToDto);
    }

    @Override
    public Mono<Boolean> deleteTransaction(Long id) {
        return transactionRepository.existsById(id)
                .flatMap(exists -> {
                    if (exists) {
                        return transactionRepository.deleteById(id).then(Mono.just(true));
                    }

                    return Mono.just(false);
                });
    }

    private Transaction mapFromDto(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setId(transactionDto.getId());
        transaction.setDate(transactionDto.getDate());
        transaction.setType(transactionDto.getType());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setBalance(transactionDto.getBalance());
        transaction.setAccountNumber(transactionDto.getAccountNumber());

        return transaction;
    }

    private TransactionDto mapToDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .date(transaction.getDate())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .balance(transaction.getBalance())
                .accountNumber(transaction.getAccountNumber())
                .build();
    }
}
