package com.jozedev.bankapp.account.service;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface ServiceClient {

    <T> Mono<T> getForEntity(String serviceKey, Class<T> responseType, Object... uriVariables);
}
