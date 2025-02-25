package com.wi.tickethahn.services.inter;

import com.wi.tickethahn.dtos.Auth.AuthenticationRequest;
import com.wi.tickethahn.dtos.Auth.AuthenticationResponse;
import com.wi.tickethahn.dtos.Auth.RegisterRequest;
import com.wi.tickethahn.entities.User;

public interface AuthenticationService {
    AuthenticationResponse login(AuthenticationRequest authenticationRequest);

    AuthenticationResponse register(RegisterRequest registerRequest);

    User getUserDbUser(String name);
}
