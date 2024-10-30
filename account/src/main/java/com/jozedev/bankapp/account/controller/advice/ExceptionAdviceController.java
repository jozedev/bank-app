package com.jozedev.bankapp.account.controller.advice;

import com.jozedev.bankapp.account.exception.NotFoundException;
import com.jozedev.bankapp.account.exception.NotEnoughFundsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.resource.NoResourceFoundException;

@ControllerAdvice
public class ExceptionAdviceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdviceController.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> catchException(Exception exception) {
        LOGGER.error("Error inesperado", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error inesperado");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> catchException(NoResourceFoundException exception) {
        LOGGER.error("Recurso no encontrado", exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el recurso solicitado");
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> catchNotFoundException(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(NotEnoughFundsException.class)
    public ResponseEntity<String> catchNotEnoughFundsException(NotEnoughFundsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }
}