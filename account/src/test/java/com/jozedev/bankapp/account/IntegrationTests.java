package com.jozedev.bankapp.account;

import com.jozedev.bankapp.account.model.dto.AccountDto;
import com.jozedev.bankapp.account.model.dto.ClientDto;
import com.jozedev.bankapp.account.model.dto.TransactionDto;
import org.junit.ClassRule;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.time.LocalDate;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationTests {

	private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationTests.class);

	private static final int ACCOUNT_PORT = 8081;
	private static final int CLIENT_PORT = 8080;
	private static final int DB_PORT = 3306;

	private final RestTemplate restTemplate = new RestTemplateBuilder().build();

	private static String ACCOUNT_URL;
	private static String CLIENT_URL;

	private static ClientDto createdClient;
	private static AccountDto createdAccount;
	private static TransactionDto createdTransaction;

	@ClassRule
	public static ComposeContainer environment =
			new ComposeContainer(new File("../docker-compose.test.yaml"))
					.withExposedService("account", ACCOUNT_PORT, Wait.forListeningPort())
					.withExposedService("client", CLIENT_PORT, Wait.forListeningPort())
					.withExposedService("db", DB_PORT, Wait.forHealthcheck())
					.withLogConsumer("account", new Slf4jLogConsumer(LOGGER))
					.withLogConsumer("client", new Slf4jLogConsumer(LOGGER))
					.withLogConsumer("db", new Slf4jLogConsumer(LOGGER));

	@BeforeAll
	public static void setUp() {
		environment.start();

		ACCOUNT_URL = "http://" + environment.getServiceHost("account", ACCOUNT_PORT)
				+ ":" +
				environment.getServicePort("account", ACCOUNT_PORT);

		CLIENT_URL = "http://" + environment.getServiceHost("client", CLIENT_PORT)
				+ ":" +
				environment.getServicePort("client", CLIENT_PORT);
	}

	@Test
	@Order(1)
	public void testCreateClient() {
		createdClient = restTemplate
				.postForEntity(CLIENT_URL + "/api/clientes", getClientDto(), ClientDto.class)
				.getBody();
		LOGGER.info("Created client: {}", createdClient);
	}

	@Test
	@Order(2)
	public void testCreateAccount() {
		// Crear cuenta
		createdAccount = restTemplate
				.postForEntity(ACCOUNT_URL + "/api/cuentas", getAccountDto(createdClient.getClientId()), AccountDto.class)
				.getBody();
		LOGGER.info("Created account: {}", createdAccount);
	}

	@Test
	@Order(3)
	public void testCreateTransaction() {
		// Crear cuenta
		createdTransaction = restTemplate
				.postForEntity(ACCOUNT_URL + "/api/movimientos", getTransactionDto(createdAccount.getAccountNumber()), TransactionDto.class)
				.getBody();
		LOGGER.info("Created transaction: {}", createdTransaction);
	}

	private ClientDto getClientDto() {
		return ClientDto.builder()
				.identityNumber("11111")
				.name("Ana Rodriguez")
				.gender("F")
				.birthDate(LocalDate.of(2000, 1, 1))
				.address("Av 123")
				.phone("999222999")
				.password("ashcbsdhcdsj")
				.active(true)
				.build();
	}

	private AccountDto getAccountDto(Long clientId) {
		return AccountDto.builder()
				.type("SAVINGS")
				.initialBalance(1000)
				.active(true)
				.clientId(clientId)
				.build();
	}

	private TransactionDto getTransactionDto(Long accountNumber) {
		return TransactionDto.builder()
				.date(LocalDate.now())
				.type("DEBITO")
				.amount(100)
				.accountNumber(accountNumber)
				.build();
	}
}
