package com.example.crud.infra.exception;

public class ViaCepUnavailableException extends RuntimeException {

    public ViaCepUnavailableException(String message) {
        super(message);
    }

    public ViaCepUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
