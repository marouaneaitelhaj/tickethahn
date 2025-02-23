package com.wi.tickethahn.services.inter;

import java.util.UUID;

import com.wi.tickethahn.dtos.Ticket.TicketReq;
import com.wi.tickethahn.entities.Ticket;

public interface TicketService {
    Ticket createTicket(TicketReq ticket);
    // Ticket readTicket(Long id);
    Ticket updateTicket(TicketReq ticket, UUID id);
    // boolean deleteTicket(Long id);
}
