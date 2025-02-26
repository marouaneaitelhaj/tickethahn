package com.wi.tickethahn.dtos.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Builder
@AllArgsConstructor
@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
}
