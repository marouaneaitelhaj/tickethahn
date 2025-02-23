package com.wi.tickethahn.dtos.User;

import java.sql.Timestamp;

import com.wi.tickethahn.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReq {
    private String username;
    private String password;
    private String email;
    private Role role;
}
