package com.wi.tickethahn.exceptions;

import java.util.List;

import lombok.Data;

@Data
public class ValidationErrorResponse {
    private List<String> errors;
}