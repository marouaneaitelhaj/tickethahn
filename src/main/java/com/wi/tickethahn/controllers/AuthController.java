package com.wi.tickethahn.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wi.tickethahn.dtos.Auth.AuthenticationRequest;
import com.wi.tickethahn.dtos.Auth.RegisterRequest;
import com.wi.tickethahn.services.inter.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<?> login(
            @RequestBody AuthenticationRequest authenticationRequest
    ) {
        return ResponseEntity.ok(authenticationService.login(authenticationRequest));
    }


    @PostMapping("/signup")
    public ResponseEntity<?> register(
            @Validated @RequestBody RegisterRequest registerRequest
    ) {
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

}