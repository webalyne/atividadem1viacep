package com.example.crud.infra.exception;

public class CepNotFoundException extends RuntimeException {

    public CepNotFoundException(String cep) {
        super("CEP não encontrado: " + cep);
    }
}
