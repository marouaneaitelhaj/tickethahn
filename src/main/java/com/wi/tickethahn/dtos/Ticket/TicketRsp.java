package com.wi.tickethahn.dtos.Ticket;

import java.time.LocalDateTime;
import java.util.UUID;

import com.wi.tickethahn.entities.User;
import com.wi.tickethahn.enums.Category;
import com.wi.tickethahn.enums.Priority;
import com.wi.tickethahn.enums.Status;

import lombok.Data;

@Data
public class TicketRsp {
    private UUID id;
    private String title;
    private String description;
    private Priority priority;
    private Category category;
    private Status status;
    private User assignedTo;
    private LocalDateTime creationDate;
}
