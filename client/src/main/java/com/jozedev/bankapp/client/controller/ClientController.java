package com.jozedev.bankapp.client.controller;

import com.jozedev.bankapp.client.exception.ClientNotFoundException;
import com.jozedev.bankapp.client.model.dto.ClientDto;
import com.jozedev.bankapp.client.model.dto.PartialClientDto;
import com.jozedev.bankapp.client.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/clientes")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<Flux<ClientDto>> getAllClients() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<Mono<ClientDto>> getClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(clientService.findClientById(clientId));
    }

    @PostMapping
    public ResponseEntity<Mono<ClientDto>> createClient(@RequestBody ClientDto client) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.saveClient(client));
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<Mono<ClientDto>> editClient(@PathVariable Long clientId, @RequestBody ClientDto client) {
        return ResponseEntity.ok(clientService.updateClient(clientId, client));
    }

    @PatchMapping("/{clientId}")
    public ResponseEntity<Mono<ClientDto>> editClient(@PathVariable Long clientId, @RequestBody PartialClientDto partialClientDto) {
        return ResponseEntity.ok(clientService.partialUpdate(clientId, partialClientDto));
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Mono<String>> deleteClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(clientService.deleteClient(clientId).handle((deleted, sink) -> {
            if (deleted) {
                sink.next("Cliente con id '%d' eliminado".formatted(clientId));
                return;
            }

            sink.error(new ClientNotFoundException("Cliente con id '%s' no encontrado", clientId));
        }));
    }
}
