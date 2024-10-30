package com.jozedev.bankapp.client.service;

import com.jozedev.bankapp.client.model.dto.ClientDto;
import com.jozedev.bankapp.client.model.dto.PartialClientDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientService {

    Flux<ClientDto> findAll();

    Mono<ClientDto> findClientById(Long clientId);

    Mono<ClientDto> saveClient(ClientDto client);

    Mono<ClientDto> updateClient(Long clientId, ClientDto client);

    Mono<ClientDto> partialUpdate(Long clientId, PartialClientDto partialClientDto);

    Mono<Boolean> deleteClient(Long clientId);
}
