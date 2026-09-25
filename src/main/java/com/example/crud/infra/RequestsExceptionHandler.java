package com.example.crud.infra;

import com.example.crud.infra.exception.CepNotFoundException;
import com.example.crud.infra.exception.InvalidCepException;
import com.example.crud.infra.exception.ProductNotFoundException;
import com.example.crud.infra.exception.ViaCepUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class RequestsExceptionHandler {

    @ExceptionHandler({ProductNotFoundException.class, CepNotFoundException.class})
    public ResponseEntity<ExceptionDTO> handleNotFound(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler({
            InvalidCepException.class,
            IllegalArgumentException.class,
            MissingServletRequestParameterException.class,
            MissingRequestHeaderException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ExceptionDTO> handleBadRequest(
            Exception exception,
            HttpServletRequest request
    ) {
        return build(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDTO> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getFieldErrors().forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));

        ExceptionDTO body = new ExceptionDTO(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Dados de entrada inválidos",
                request.getRequestURI(),
                fields
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ViaCepUnavailableException.class)
    public ResponseEntity<ExceptionDTO> handleViaCepUnavailable(
            ViaCepUnavailableException exception,
            HttpServletRequest request
    ) {
        return build(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage(), request);
    }

    private ResponseEntity<ExceptionDTO> build(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(status)
                .body(new ExceptionDTO(status.value(), message, request.getRequestURI()));
    }
}
