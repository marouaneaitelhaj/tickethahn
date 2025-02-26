package com.wi.tickethahn.exceptions;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;
import com.wi.tickethahn.exceptions.DuplicatedDataEx;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundEx.class})
    public Map<String, String> handleNotFoundEx(RuntimeException e) {
        return Map.of("error", "Error: " + e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Map<String, String> handle(DataIntegrityViolationException e) {
        return Map.of("error", "Error: " + e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(org.springframework.validation.BindException.class)
    public ResponseEntity<ValidationErrorResponse> handle(org.springframework.validation.BindException ex) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        errorResponse.setErrors(ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .map(msg -> "Error: " + msg)
                .collect(Collectors.toList()));

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public Map<String, String> handle(HttpMessageNotReadableException e) {
        return Map.of("error", "Error: Malformed JSON request");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicatedDataEx.class)
    public Map<String, String> handleDuplicatedDataEx(RuntimeException e) {
        return Map.of("error", "Error: " + e.getMessage());
    }
}
