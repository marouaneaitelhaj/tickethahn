package com.wi.tickethahn.dtos.AuditLog;

import java.sql.Timestamp;
import java.util.UUID;

import com.wi.tickethahn.dtos.Ticket.TicketReq;
import com.wi.tickethahn.enums.Action;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogRes {
    private UUID id;

    private TicketReq ticket;

    private Action action;

    private String oldValue;

    private String newValue;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}