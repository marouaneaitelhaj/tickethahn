package com.wi.tickethahn.exceptions;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.MethodArgumentNotValidException;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundEx.class})
    public Map<String, String> handleNotFoundEx(RuntimeException e) {
        return Map.of("error", e.getMessage());
    }

    // @ExceptionHandler(DataIntegrityViolationException.class)
    // @ResponseStatus(HttpStatus.BAD_REQUEST)
    // public Map<String, String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
    //     return Map.of("error", e.getMessage());
    // }

    // @ExceptionHandler(MethodArgumentNotValidException.class)
    // public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        
    //     List<String> errorDetails = ex.getBindingResult().getFieldErrors()
    //         .stream()
    //         .map(FieldError::getDefaultMessage)
    //         .collect(Collectors.toList());

    //     ValidationErrorResponse errorResponse = new ValidationErrorResponse(
    //         "Validation Failed",
    //         errorDetails
    //     );

    //     return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    // }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Map<String, String> handle(DataIntegrityViolationException e) {
        return Map.of("error", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(org.springframework.validation.BindException.class)
    public ResponseEntity<ValidationErrorResponse> handle(org.springframework.validation.BindException ex) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        errorResponse.setErrors(ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList()));

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public Map<String, String> handle(HttpMessageNotReadableException e) {
        return Map.of("error", "Malformed JSON request");
    }

}