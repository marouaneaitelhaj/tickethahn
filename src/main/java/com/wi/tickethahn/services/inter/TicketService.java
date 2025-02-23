package com.wi.tickethahn.services.inter;

import com.wi.tickethahn.dtos.Ticket.TicketReq;
import com.wi.tickethahn.entities.Ticket;

public interface TicketService {
    Ticket createTicket(TicketReq ticket);
    // Ticket readTicket(Long id);
    // Ticket updateTicket(TicketReq ticket);
    // boolean deleteTicket(Long id);
}
