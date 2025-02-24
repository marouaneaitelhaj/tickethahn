package com.wi.tickethahn.dtos.Ticket;

import java.util.UUID;

import com.wi.tickethahn.enums.Status;

import lombok.Data;

@Data
public class TicketStatusUpdateRequest {
    private UUID id;
    private Status status;
}