package com.jozedev.bankapp.account.controller;

import com.jozedev.bankapp.account.exception.NotFoundException;
import com.jozedev.bankapp.account.model.dto.ReportDto;
import com.jozedev.bankapp.account.model.dto.TransactionDto;
import com.jozedev.bankapp.account.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/movimientos")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<Flux<TransactionDto>> getAllAccounts() {
        return ResponseEntity.ok(transactionService.findAll());
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Mono<TransactionDto>> getAccount(@PathVariable Long transactionId) {
        return ResponseEntity.ok(transactionService.findTransactionById(transactionId));
    }

    @GetMapping("/reportes")
    public ResponseEntity<Flux<ReportDto>> report(@RequestParam Long clientId,
                                  @RequestParam LocalDate dateStart,
                                  @RequestParam LocalDate dateEnd) {
        return ResponseEntity.ok(transactionService.getReportOfClient(clientId, dateStart, dateEnd));
    }

    @PostMapping
    public ResponseEntity<Mono<TransactionDto>> createAccount(@RequestBody TransactionDto account) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.saveTransaction(account));
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Mono<String>> deleteAccount(@PathVariable Long transactionId) {
        return ResponseEntity.ok(transactionService.deleteTransaction(transactionId).handle((deleted, sink) -> {
            if (deleted) {
                sink.next("Transacción con número '%d' eliminada".formatted(transactionId));
                return;
            }

            sink.error(new NotFoundException("Transacción con número '%s' no encontrada", transactionId));
        }));
    }
}
