package com.wi.tickethahn.enums;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
    Employees,IT_Support;

    
    public List<SimpleGrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.name()));
    }
}