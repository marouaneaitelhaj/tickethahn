package com.example.entities;

import java.sql.Timestamp;


import com.example.enums.Action;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    private String id;

    private Ticket ticket;


    private Action action;

    private String oldValue;

    private String newValue;

    private Timestamp createdAt;
    private Timestamp updatedAt;
}