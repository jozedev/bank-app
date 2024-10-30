package com.jozedev.bankapp.account.service;

import com.jozedev.bankapp.account.model.dto.AccountDto;
import com.jozedev.bankapp.account.model.dto.PartialAccountDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {

    Flux<AccountDto> findAll();

    Flux<AccountDto> findByClientId(Long clientId);

    Mono<AccountDto> findAccountByNumber(Long accountNumber);

    Mono<AccountDto> saveAccount(AccountDto account);

    Mono<AccountDto> updateAccount(Long accountNumber, AccountDto account);

    Mono<AccountDto> partialUpdate(Long accountNumber, PartialAccountDto partialAccount);

    Mono<Boolean> deleteAccount(Long accountNumber);
}
