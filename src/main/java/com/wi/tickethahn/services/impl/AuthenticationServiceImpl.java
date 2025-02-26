package com.wi.tickethahn.services.impl;


import lombok.AllArgsConstructor;
import lombok.Data;
import com.wi.tickethahn.configs.JwtService;
import com.wi.tickethahn.dtos.Auth.AuthenticationRequest;
import com.wi.tickethahn.dtos.Auth.AuthenticationResponse;
import com.wi.tickethahn.dtos.Auth.RegisterRequest;
import com.wi.tickethahn.entities.User;
import com.wi.tickethahn.enums.Role;
import com.wi.tickethahn.exceptions.DuplicatedDataEx;
import com.wi.tickethahn.repositories.UserRepository;
import com.wi.tickethahn.services.inter.AuthenticationService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Data
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private  final UserRepository userRepository;


    private PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    private final JwtService jwtService;


    private  final AuthenticationManager authenticationManager;
    @Override
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
       authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        var user = userRepository.findByUsername(authenticationRequest.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


    @Override
    public AuthenticationResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicatedDataEx("Email already exists");
        }
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicatedDataEx("Username already exists");
        }
        User user = modelMapper.map(registerRequest, User.class);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(user.getRole());
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public User getUserDbUser(String name) {
        return userRepository.findByUsername(name).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
