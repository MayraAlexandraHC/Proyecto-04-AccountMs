package com.bank.AccountMs.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CuentaException.class)
    public ResponseEntity<ErrorResponse> handleCuentaException(CuentaException ex) {
        ErrorResponse error = buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleSaldoInsuficienteException(SaldoInsuficienteException ex) {
        ErrorResponse error = buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = "Error en el formato de datos: ";

        if (ex.getMessage().contains("tipoCuenta")) {
            message += "El tipo de cuenta debe ser AHORROS o CORRIENTE";
        } else if (ex.getMessage().contains("clienteId")) {
            message += "El ID del cliente debe ser un número entero";
        } else if (ex.getMessage().contains("monto")) {
            message += "El monto debe ser un número válido";
        } else {
            message += "Formato de datos inválido";
        }

        ErrorResponse error = buildErrorResponse(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("El parámetro '%s' debe ser de tipo %s",
                ex.getName(),
                ex.getRequiredType().getSimpleName());

        ErrorResponse error = buildErrorResponse(message, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = buildErrorResponse(
                "Error interno del servidor",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse buildErrorResponse(String message, HttpStatus status) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(message)
                .status(status.value())
                .build();
    }

    @lombok.Data
    @lombok.Builder
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private String message;
        private int status;
    }
}