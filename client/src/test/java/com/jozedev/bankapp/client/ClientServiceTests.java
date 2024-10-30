package com.jozedev.bankapp.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.jozedev.bankapp.client.exception.ClientNotFoundException;
import com.jozedev.bankapp.client.model.Client;
import com.jozedev.bankapp.client.model.dto.ClientDto;
import com.jozedev.bankapp.client.repository.ClientRepository;
import com.jozedev.bankapp.client.service.ClientService;
import com.jozedev.bankapp.client.service.ClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ClientServiceTests {

    private final ClientRepository clientRepository = mock(ClientRepository.class);
    private final ClientService clientService = new ClientServiceImpl(clientRepository);

    @Test
    public void findAll_existsData_shouldReturnIdenticalList() {

        when(clientRepository.findAll()).thenReturn(Flux.fromIterable(getClientList()));

        List<ClientDto> expectedResponse = getClientDtoList();
        List<ClientDto> actualResponse = clientService.findAll().collectList().block();

        assertArrayEquals(new List[]{expectedResponse}, new List[]{actualResponse});
    }

    @Test
    public void findById_existsData_shouldReturnClient() {

        when(clientRepository.findById(1L)).thenReturn(Mono.just(getClient()));

        ClientDto expectedResponse = getClientDto();
        ClientDto actualResponse = clientService.findClientById(1L).block();

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void findById_doesntExistData_throwsClientNotFoundException() {
        when(clientRepository.findById(1L)).thenReturn(Mono.empty());
        assertThrows(ClientNotFoundException.class, () -> clientService.findClientById(1L).block());
    }

    @Test
    public void saveClient_correctData_returnsClient() {

        Client client = getClient();
        when(clientRepository.save(any())).thenReturn(Mono.just(client));

        ClientDto expectedResponse = getClientDto();
        ClientDto actualResponse = clientService.saveClient(getClientDto()).block();

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void updateClient_existsData_returnsClient() {

        Client client = getClient();
        when(clientRepository.save(any())).thenReturn(Mono.just(client));
        when(clientRepository.findById(1L)).thenReturn(Mono.just(client));

        ClientDto expectedResponse = getClientDto();
        ClientDto actualResponse = clientService.updateClient(1L, getClientDto()).block();

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void updateClient_doesntExistData_throwsClientNotFoundException() {
        when(clientRepository.findById(1L)).thenReturn(Mono.empty());
        assertThrows(ClientNotFoundException.class, () -> clientService.updateClient(1L, getClientDto()).block());
    }

    @Test
    public void deleteClient_existsData_returnsTrue() {

        when(clientRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(clientRepository.deleteById(1L)).thenReturn(Mono.empty());

        boolean response = clientService.deleteClient(1L).block();

        assertTrue(response);
    }

    @Test
    public void deleteClient_doesntExistData_returnsFalse() {

        when(clientRepository.existsById(1L)).thenReturn(Mono.just(false));

        boolean response = clientService.deleteClient(1L).block();

        assertFalse(response);
    }

    private Client getClient() {
        Client client = new Client();
        client.setClientId(1L);
        client.setIdentityNumber("11111");
        client.setName("client");
        client.setGender("F");
        client.setBirthDate(LocalDate.of(2000, 1, 1));
        client.setAddress("Av 123");
        client.setPhone("999222999");
        client.setPassword("ashcbsdhcdsj");
        client.setActive(true);

        return client;
    }

    private ClientDto getClientDto() {
        return ClientDto.builder()
                .clientId(1L)
                .identityNumber("11111")
                .name("client")
                .gender("F")
                .birthDate(LocalDate.of(2000, 1, 1))
                .address("Av 123")
                .phone("999222999")
                .password("ashcbsdhcdsj")
                .active(true)
                .build();
    }

    private List<Client> getClientList() {
        List<Client> clientsList = new ArrayList<>();
        clientsList.add(getClient());

        return clientsList;
    }

    private List<ClientDto> getClientDtoList() {
        List<ClientDto> clientDtoList = new ArrayList<>();
        clientDtoList.add(getClientDto());

        return clientDtoList;
    }
}
