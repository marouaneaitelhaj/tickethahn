package com.wi.tickethahn.services.inter;

import java.util.UUID;

import com.wi.tickethahn.dtos.Ticket.TicketReq;
import com.wi.tickethahn.entities.Ticket;
import java.util.List;
import com.wi.tickethahn.enums.Status;

public interface TicketService {
    Ticket createTicket(TicketReq ticket);
    Ticket updateTicket(TicketReq ticket, UUID id);
    void checkIfStatusChanged(Ticket ticket, Status new_status);
    List<Ticket> getTicketByUser(UUID id);
    List<Ticket> findAll();
    Ticket findById(UUID id);
    List<Ticket> findByStatus(Status status);
}
