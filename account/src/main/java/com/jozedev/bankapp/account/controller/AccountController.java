package com.jozedev.bankapp.account.controller;

import com.jozedev.bankapp.account.exception.NotFoundException;
import com.jozedev.bankapp.account.model.dto.AccountDto;
import com.jozedev.bankapp.account.model.dto.PartialAccountDto;
import com.jozedev.bankapp.account.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/cuentas")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<Flux<AccountDto>> getAllAccounts() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Mono<AccountDto>> getAccount(@PathVariable Long accountNumber) {
        return ResponseEntity.ok(accountService.findAccountByNumber(accountNumber));
    }

    @PostMapping
    public ResponseEntity<Mono<AccountDto>> createAccount(@RequestBody AccountDto account) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.saveAccount(account));
    }

    @PutMapping("/{accountNumber}")
    public ResponseEntity<Mono<AccountDto>> updateAccount(@PathVariable Long accountNumber, @RequestBody AccountDto account) {
        return ResponseEntity.ok(accountService.updateAccount(accountNumber, account));
    }

    @PatchMapping("/{accountNumber}")
    public ResponseEntity<Mono<AccountDto>> partialUpdateAccount(@PathVariable Long accountNumber, @RequestBody PartialAccountDto partialAccount) {
        return ResponseEntity.ok(accountService.partialUpdate(accountNumber, partialAccount));
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Mono<String>> deleteAccount(@PathVariable Long accountNumber) {
        return ResponseEntity.ok(accountService.deleteAccount(accountNumber).handle((deleted, sink) -> {
            if (deleted) {
                sink.next("Cliente con número '%d' eliminado".formatted(accountNumber));
                return;
            }

            sink.error(new NotFoundException("Cliente con número '%s' no encontrado", accountNumber));
        }));
    }
}
