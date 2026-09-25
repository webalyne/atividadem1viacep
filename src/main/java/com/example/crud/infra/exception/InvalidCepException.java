package com.example.crud.infra.exception;

public class InvalidCepException extends RuntimeException {

    public InvalidCepException() {
        super("O CEP deve conter oito dígitos, com hífen opcional");
    }
}
