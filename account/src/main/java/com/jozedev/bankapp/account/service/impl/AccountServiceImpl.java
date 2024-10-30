package com.jozedev.bankapp.account.service.impl;

import com.jozedev.bankapp.account.exception.NotActiveException;
import com.jozedev.bankapp.account.exception.NotFoundException;
import com.jozedev.bankapp.account.model.Account;
import com.jozedev.bankapp.account.model.dto.AccountDto;
import com.jozedev.bankapp.account.model.dto.ClientDto;
import com.jozedev.bankapp.account.model.dto.PartialAccountDto;
import com.jozedev.bankapp.account.repository.AccountRepository;
import com.jozedev.bankapp.account.service.AccountService;
import com.jozedev.bankapp.account.service.ServiceClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final ServiceClient serviceClient;

    public AccountServiceImpl(AccountRepository accountRepository, ServiceClient serviceClient) {
        this.accountRepository = accountRepository;
        this.serviceClient = serviceClient;
    }

    @Override
    @Transactional
    public Flux<AccountDto> findAll() {
        return accountRepository.findAll().map(this::mapToDto);
    }

    @Override
    @Transactional
    public Flux<AccountDto> findByClientId(Long clientId) {
        return accountRepository.findByClientId(clientId).map(this::mapToDto);
    }

    @Override
    @Transactional
    public Mono<AccountDto> findAccountByNumber(Long accountNumber) {
        return accountRepository.findById(accountNumber).map(this::mapToDto);
    }

    @Override
    @Transactional
    public Mono<AccountDto> saveAccount(AccountDto account) {
        return serviceClient.getForEntity("client-info", ClientDto.class, account.getClientId())
                .switchIfEmpty(Mono.error(new NotFoundException("No se encontró el cliente de id: %d".formatted(account.getClientId()))))
                .flatMap(client -> {
                    if (!client.isActive()) {
                        return Mono.error(new NotActiveException("El cliente con id '%d' no está activo".formatted(account.getClientId())));
                    }

                    return accountRepository.save(mapFromDto(account));
                })
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public Mono<AccountDto> updateAccount(Long accountNumber, AccountDto account) {
        return accountRepository.findById(accountNumber)
                .switchIfEmpty(Mono.error(new NotFoundException("Cuenta de id '%s' no encontrada", accountNumber)))
                .flatMap(oldAccount -> { // Validate the client existence in case the client id has changed
                    if (oldAccount.getClientId().equals(account.getClientId())) {
                        return Mono.just(oldAccount);
                    } else {
                        return serviceClient.getForEntity("client-info", ClientDto.class, account.getClientId())
                                .switchIfEmpty(Mono.error(new NotFoundException("No se encontró el cliente de id: %d".formatted(account.getClientId()))))
                                .flatMap(client -> {
                                    if (!client.isActive()) {
                                        return Mono.error(new NotActiveException("El cliente con id '%d' no está activo".formatted(account.getClientId())));
                                    }

                                    return Mono.just(oldAccount);
                                });
                    }
                })
                .map((oldAccount) -> {
                    account.setAccountNumber(accountNumber);
                    return mapFromDto(account);
                })
                .flatMap(accountRepository::save)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public Mono<AccountDto> partialUpdate(Long accountNumber, PartialAccountDto partialAccount) {
        return accountRepository.findById(accountNumber)
                .switchIfEmpty(Mono.error(new NotFoundException("Cuenta de id '%s' no encontrada", accountNumber)))
                .map(oldAccount -> {
                    oldAccount.setAccountNumber(accountNumber);
                    oldAccount.setActive(partialAccount.isActive());
                    return oldAccount;
                })
                .flatMap(accountRepository::save)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public Mono<Boolean> deleteAccount(Long accountNumber) {
        return accountRepository.existsById(accountNumber)
                .flatMap(exists -> {
                    if (exists) {
                        return accountRepository.deleteById(accountNumber).then(Mono.just(true));
                    }

                    return Mono.just(false);
                });
    }

    private Account mapFromDto(AccountDto accountDto) {
        Account account = new Account();
        account.setAccountNumber(accountDto.getAccountNumber());
        account.setType(accountDto.getType());
        account.setInitialBalance(accountDto.getInitialBalance());
        account.setActive(accountDto.isActive());
        account.setClientId(accountDto.getClientId());

        return account;
    }

    private AccountDto mapToDto(Account account) {
        return AccountDto.builder()
                .accountNumber(account.getAccountNumber())
                .type(account.getType())
                .initialBalance(account.getInitialBalance())
                .active(account.isActive())
                .clientId(account.getClientId())
                .build();
    }
}
