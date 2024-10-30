package com.jozedev.bankapp.account;

import com.jozedev.bankapp.account.configuration.ByteBooleanConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.r2dbc.core.DatabaseClient;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class AccountApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountApplication.class, args);
	}

	@Bean
	public R2dbcCustomConversions r2dbcCustomConversions(DatabaseClient databaseClient) {
		R2dbcDialect dialect = DialectResolver.getDialect(databaseClient.getConnectionFactory());

		List<Object> converters = new ArrayList<>();
		converters.add(new ByteBooleanConverter());

		return new R2dbcCustomConversions(CustomConversions.StoreConversions.of(dialect.getSimpleTypeHolder()), converters);
	}

}
