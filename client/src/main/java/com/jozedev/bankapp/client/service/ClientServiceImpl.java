package com.jozedev.bankapp.client.service;

import com.jozedev.bankapp.client.exception.ClientNotFoundException;
import com.jozedev.bankapp.client.model.Client;
import com.jozedev.bankapp.client.model.dto.ClientDto;
import com.jozedev.bankapp.client.model.dto.PartialClientDto;
import com.jozedev.bankapp.client.repository.ClientRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Flux<ClientDto> findAll() {
        return clientRepository.findAll().map(this::mapToDto);
    }

    @Override
    public Mono<ClientDto> findClientById(Long clientId) {
        Mono<Client> clientOpt = clientRepository.findById(clientId)
                .switchIfEmpty(Mono.error(new ClientNotFoundException("Cliente con id '%s' no encontrado", clientId)));
        return clientOpt.map(this::mapToDto);
    }

    @Override
    public Mono<ClientDto> saveClient(ClientDto client) {
        Client clientEntity = mapFromDto(client);
        return clientRepository.save(clientEntity).map(this::mapToDto);
    }

    @Override
    public Mono<ClientDto> updateClient(Long clientId, ClientDto client) {
        return clientRepository.findById(clientId)
                .switchIfEmpty(Mono.error(new ClientNotFoundException("Cliente con id '%s' no encontrado", clientId)))
                .map(clientEntity -> {
                    client.setClientId(clientId);
                    return mapFromDto(client);
                })
                .flatMap(clientRepository::save)
                .map(this::mapToDto);
    }

    @Override
    public Mono<ClientDto> partialUpdate(Long clientId, PartialClientDto partialClientDto) {
        return clientRepository.findById(clientId)
                .switchIfEmpty(Mono.error(new ClientNotFoundException("Cliente con id '%s' no encontrado", clientId)))
                .map(client -> {
                    client.setClientId(clientId);
                    client.setActive(partialClientDto.isActive());
                    return client;
                })
                .flatMap(clientRepository::save)
                .map(this::mapToDto);
    }

    @Override
    public Mono<Boolean> deleteClient(Long clientId) {
        return clientRepository.existsById(clientId)
                .flatMap(exists -> {
                    if (exists) {
                        return clientRepository.deleteById(clientId).then(Mono.just(true));
                    }

                    return Mono.just(false);
                });
    }

    private Client mapFromDto(ClientDto clientDto) {
        Client client = new Client();
        client.setName(clientDto.getName());
        client.setClientId(clientDto.getClientId());
        client.setPassword(clientDto.getPassword());
        client.setActive(clientDto.isActive());
        client.setIdentityNumber(clientDto.getIdentityNumber());
        client.setGender(clientDto.getGender());
        client.setBirthDate(clientDto.getBirthDate());
        client.setAddress(clientDto.getAddress());
        client.setPhone(clientDto.getPhone());

        return client;
    }

    private ClientDto mapToDto(Client client) {
        return ClientDto.builder()
                .clientId(client.getClientId())
                .identityNumber(client.getIdentityNumber())
                .name(client.getName())
                .gender(client.getGender())
                .birthDate(client.getBirthDate())
                .address(client.getAddress())
                .password(client.getPassword())
                .phone(client.getPhone())
                .active(client.isActive())
                .build();
    }
}
