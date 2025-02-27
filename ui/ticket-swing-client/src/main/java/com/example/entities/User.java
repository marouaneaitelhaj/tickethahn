package com.example.entities;


import java.sql.Timestamp;


import com.example.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String username;
    private String password;
    private String email;
    private Role role;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public User(String id, String username) {
        this.id = id;
        this.username = username;
    }
}
