package com.wi.tickethahn.entity;


import com.wi.tickethahn.enums.Role;

import lombok.Data;


@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private Role[] role;
}
