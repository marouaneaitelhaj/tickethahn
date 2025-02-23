package com.wi.tickethahn.entities;

import java.util.Date;
import java.util.UUID;

import com.wi.tickethahn.enums.Action;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Long ticketId;
    private Action action;
    private String oldValue;
    private String newValue;
    private Date timestamp = new Date();
}